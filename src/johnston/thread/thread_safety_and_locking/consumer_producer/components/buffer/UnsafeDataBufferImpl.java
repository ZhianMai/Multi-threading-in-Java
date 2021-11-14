package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of not thread-safe buffer. FIFO.
 */
public class UnsafeDataBufferImpl<D> implements DataBuffer<D> {
  public static final int DEFAULT_BUFFER_CAPACITY = 10;

  private List<D> dataList;
  private AtomicInteger size;
  private int capacity;

  public UnsafeDataBufferImpl() {
    this(DEFAULT_BUFFER_CAPACITY);
  }

  public UnsafeDataBufferImpl(int capacity) {
    this.capacity = capacity;
    dataList = new LinkedList<>();
    size = new AtomicInteger(0);
  }

  @Override
  public D get() {
    checkSizeConsistency();

    if (size.get() <= 0) {
      return null;
    }

    size.decrementAndGet();
    return dataList.remove(0);
  }

  @Override
  public boolean put(D data) {
    checkSizeConsistency();

    if (size.get() >= capacity) {
      return false;
    }

    size.incrementAndGet();
    dataList.add(data);
    return true;
  }

  @Override
  public void clear() {
    dataList.clear();
    size.set(0);
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
