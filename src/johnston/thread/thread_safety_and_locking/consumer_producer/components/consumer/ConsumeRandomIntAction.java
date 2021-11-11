package johnston.thread.thread_safety_and_locking.consumer_producer.components.consumer;

import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.DataBuffer;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Callable class of consuming integer action for Consumer class.
 */
public class ConsumeRandomIntAction implements Callable {
  private DataBuffer<Integer> dataBuffer;
  private Random random;

  public ConsumeRandomIntAction(DataBuffer<Integer> dataBuffer) {
    this.dataBuffer = dataBuffer;
    random = new Random();
  }

  @Override
  public Object call() throws Exception {
    Integer prevData = null;

    try {
      prevData = dataBuffer.get();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return prevData;
  }
}
