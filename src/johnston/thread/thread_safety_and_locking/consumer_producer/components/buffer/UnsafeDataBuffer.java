package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.DataBuffer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of not thread-safe buffer. FIFO.
 */
public class UnsafeDataBuffer<D> implements DataBuffer<D> {
  public static final int DEFAULT_BUFFER_CAPACITY = 10;

  private List<D> dataList;
  private AtomicInteger size;
  private int capacity;

  public UnsafeDataBuffer() {
    this(DEFAULT_BUFFER_CAPACITY);
  }

  public UnsafeDataBuffer(int capacity) {
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

  private void checkSizeConsistency() {
    if (size.get() != dataList.size()) {
      throw new RuntimeException("Size inconsistency in buffer occurred!");
    }
  }
}
