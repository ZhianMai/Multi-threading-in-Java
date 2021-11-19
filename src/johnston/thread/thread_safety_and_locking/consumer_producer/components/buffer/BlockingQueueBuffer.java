package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueueBuffer<D> implements DataBuffer {
  public static final int DEFAULT_BUFFER_CAPACITY = 10;
  private ArrayBlockingQueue<D> buffer;
  private final int capacity;

  public BlockingQueueBuffer() {
    this(DEFAULT_BUFFER_CAPACITY);
  }

  public BlockingQueueBuffer(int capacity) {
    buffer = new ArrayBlockingQueue<>(capacity);
    this.capacity = capacity;
  }

  @Override
  public D get() {
    D data = null;
    try {
      data = buffer.take();
    } catch (InterruptedException e) {
      return null;
    }
    return data;
  }

  @Override
  public boolean put(Object data) {
    return buffer.offer((D)data);
  }

  @Override
  public void clear() {
    buffer.clear();
  }

  @Override
  public int size() {
    return buffer.size();
  }

  @Override
  public int capacity() {
    return capacity;
  }
}
