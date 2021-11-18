package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Using ReentrantLock which is a explicit lock to ensure thread-safety. It uses Java lightweight
 * lock (spinlock) to block threads instead of heavyweight lock.
 *
 * Changing lock.lock() to lock.tryLock() can switch to non-blocking lock.
 */
public class ReentrantSafeDataBufferImpl<D> implements DataBuffer {
  public static final int DEFAULT_BUFFER_CAPACITY = 10;
  DataBuffer<D> unsafeDataBuffer;
  private final Lock WRITE_LOCK;
  private final Lock PUT_WAITING;
  private final Lock GET_WAITING;
  private final Condition PUT_CONDITION;
  private final Condition GET_CONDITION;

  public ReentrantSafeDataBufferImpl() {
    this(DEFAULT_BUFFER_CAPACITY);
  }

  public ReentrantSafeDataBufferImpl(int capacity) {
    unsafeDataBuffer = new UnsafeDataBufferImpl<>(capacity);
    WRITE_LOCK = new ReentrantLock();
    PUT_WAITING = new ReentrantLock();
    GET_WAITING = new ReentrantLock();
    PUT_CONDITION = PUT_WAITING.newCondition();
    GET_CONDITION = GET_WAITING.newCondition();
  }

  @Override
  public D get() {
    D data = null;
    while (size() == 0) {
      GET_WAITING.lock();
      try {
        GET_CONDITION.await();
      } catch (InterruptedException e) {
        return null;
      } finally {
        GET_WAITING.unlock();
      }
    }

    WRITE_LOCK.lock();
    try {
      data = unsafeDataBuffer.get();
    } finally {
      WRITE_LOCK.unlock();
    }

    PUT_WAITING.lock();
    try {
      PUT_CONDITION.signalAll();
    } finally {
      PUT_WAITING.unlock();
    }

    return data;
  }

  @Override
  public boolean put(Object data) {
    boolean result = false;
    while (size() == capacity()) {
      PUT_WAITING.lock();
      try {
        PUT_CONDITION.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        PUT_WAITING.unlock();
      }
    }

    WRITE_LOCK.lock();
    try {
      result = unsafeDataBuffer.put((D) data);
    } finally {
      WRITE_LOCK.unlock();
    }

    GET_WAITING.lock();
    try {
      GET_CONDITION.signalAll();
    } finally {
      GET_WAITING.unlock();
    }

    return result;
  }

  @Override
  public void clear() {
    WRITE_LOCK.lock();
    try {
      unsafeDataBuffer.clear();
    } finally {
      WRITE_LOCK.unlock();
    }
  }

  @Override
  public int size() {
    WRITE_LOCK.lock();
    try {
      return unsafeDataBuffer.size();
    } finally {
      WRITE_LOCK.unlock();
    }
  }

  @Override
  public int capacity() {
    return unsafeDataBuffer.capacity();
  }
}
