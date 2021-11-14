package johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer;

/**
 * A better approach of buffer implementation. It blocks the producer thread if the buffer is full,
 * and blocks the consumer thread if the buffer is empty. It ensures that every call from consumer
 * and producer can proceed instead of returning null or false.
 */
public class WaitingSafeDataBufferImpl<D> implements DataBuffer {
  public static final int DEFAULT_BUFFER_CAPACITY = 10;
  DataBuffer<D> unsafeDataBuffer;
  private final Object LOCK;
  private final Object PUT_WAITING;
  private final Object GET_WAITING;

  public WaitingSafeDataBufferImpl() {
    this(DEFAULT_BUFFER_CAPACITY);
  }

  public WaitingSafeDataBufferImpl(int capacity) {
    unsafeDataBuffer = new UnsafeDataBufferImpl<>(capacity);
    LOCK = new Object();
    PUT_WAITING = new Object();
    GET_WAITING = new Object();
  }

  @Override
  public D get() {
    D data = null;
    while (size() == 0) {
      synchronized (GET_WAITING) {
        try {
          GET_WAITING.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    synchronized (LOCK) {
      data = unsafeDataBuffer.get();
    }

    synchronized (PUT_WAITING) {
      PUT_WAITING.notify();
    }

    return data;
  }

  @Override
  public boolean put(Object data) {
    boolean result = false;
    while (size() == capacity()) {
      synchronized (PUT_WAITING) {
        try {
          PUT_WAITING.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    synchronized (LOCK) {
      result = unsafeDataBuffer.put((D) data);
    }

    synchronized (GET_WAITING) {
      GET_WAITING.notify();
    }
    return result;
  }

  @Override
  public void clear() {
    unsafeDataBuffer.clear();
  }

  @Override
  public int size() {
    return unsafeDataBuffer.size();
  }

  @Override
  public int capacity() {
    return unsafeDataBuffer.capacity();
  }
}
