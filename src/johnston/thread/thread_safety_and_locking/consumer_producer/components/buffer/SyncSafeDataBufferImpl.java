package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

/**
 * Implementation of thread-safe data buffer. FIFO. Simply sync the produce and consume method.
 * It serializes the read and write method, which has a great performance penalty.
 */
public class SyncSafeDataBufferImpl<D> implements DataBuffer {
  DataBuffer<D> dataBuffer = new UnsafeDataBufferImpl<>();

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
}
