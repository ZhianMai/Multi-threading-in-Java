package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

public interface DataBuffer<D> {
  public D get();
  public boolean put(D data);
  public void clear();
}
