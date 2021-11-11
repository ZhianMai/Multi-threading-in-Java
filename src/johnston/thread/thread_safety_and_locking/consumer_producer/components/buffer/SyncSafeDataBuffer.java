package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

public class SyncSafeDataBuffer<D> implements DataBuffer {
  DataBuffer<D> dataBuffer = new UnsafeDataBuffer<>();

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
