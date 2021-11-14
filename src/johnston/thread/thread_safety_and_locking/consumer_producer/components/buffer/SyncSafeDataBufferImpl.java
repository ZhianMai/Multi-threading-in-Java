package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

import johnston.thread.thread_safety_and_locking.consumer_producer.SyncedConsumingProducing;

/**
 * Implementation of thread-safe data buffer. FIFO. Simply sync the produce and consume method.
 * It serializes the read and write method, which has a great performance penalty. It also exists
 * useless query: if the buffer is full, then reject to insert; if the buffer is empty, then return
 * null.
 */
public class SyncSafeDataBufferImpl<D> implements DataBuffer {
  public static final int DEFAULT_BUFFER_CAPACITY = 10;
  DataBuffer<D> dataBuffer;

  public SyncSafeDataBufferImpl() {
    this(DEFAULT_BUFFER_CAPACITY);
  }

  public SyncSafeDataBufferImpl(int capacity) {
    dataBuffer = new UnsafeDataBufferImpl<>(capacity);
  }

  @Override
  public synchronized D get() {
    return dataBuffer.get();
  }

  @Override
  public synchronized boolean put(Object data) {
    return dataBuffer.put((D) data);
  }

  @Override
  public synchronized void clear() {
    dataBuffer.clear();
  }

  @Override
  public int size() {
    return dataBuffer.size();
  }

  @Override
  public int capacity() {
    return dataBuffer.capacity();
  }
}
