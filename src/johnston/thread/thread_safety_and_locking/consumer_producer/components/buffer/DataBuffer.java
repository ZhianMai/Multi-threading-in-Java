package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

/**
 * Interface for data buffer in consumer-producer.
 */
public interface DataBuffer<D> {
  /**
   * Return an element for consumer.
   */
  public D get();

  /**
   * Store an element to producer.
   */
  public boolean put(D data);

  /**
   * Empty the buffer.
   */
  public void clear();

  /**
   * Return size
   */
  public int size();

  /**
   * Return capacity
   */
  public int capacity();
}
