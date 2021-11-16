package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of not thread-safe buffer. FIFO.
 */
public class UnsafeDataBufferImpl<D> implements DataBuffer<D> {
  public static final int DEFAULT_BUFFER_CAPACITY = 100;

  private Object[] dataList;
  private AtomicInteger size;
  private int capacity;
  private int headIdx = 0;
  private int tailIdx = 0;

  public UnsafeDataBufferImpl() {
    this(DEFAULT_BUFFER_CAPACITY);
  }

  public UnsafeDataBufferImpl(int capacity) {
    this.capacity = capacity;
    dataList = new Object[capacity];
    size = new AtomicInteger(0);
  }

  @Override
  public D get() {
    checkSizeConsistency();

    if (size.get() <= 0) {
      return null;
    }

    size.decrementAndGet();
    Object d = dataList[headIdx];
    dataList[headIdx] = null;
    headIdx = (++headIdx) % capacity;
    return (D)d;
  }

  @Override
  public boolean put(D data) {
    checkSizeConsistency();

    if (size.get() >= capacity) {
      return false;
    }

    size.incrementAndGet();
    dataList[tailIdx] = data;
    tailIdx = (++tailIdx) % capacity;
    return true;
  }

  @Override
  public void clear() {
    dataList = new Object[capacity];
    size.set(0);
    headIdx = 0;
    tailIdx = 0;
  }

  @Override
  public int size() {
    return size.get();
  }

  @Override
  public int capacity() {
    return capacity;
  }

  private void checkSizeConsistency() {
    if (size() > capacity) {
      throw new RuntimeException("Buffer overflow!");
    }

    if (size() < 0) {
      throw new RuntimeException("Negative size!");
    }
  }
}
