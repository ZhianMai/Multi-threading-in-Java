package johnston.thread.thread_safety_and_locking.consumer_producer.components.producer;

import johnston.thread.thread_safety_and_locking.consumer_producer.components.buffer.DataBuffer;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Callable class of producing random integer action for Producer class. Need to specify the time
 * needed to produce an integer.
 */
public class ProduceRandomIntAction implements Callable {
  private DataBuffer<Integer> dataBuffer;
  private Random random;
  private int produceDuration;
  public static final int DEFAULT_PRODUCE_DURATION_MILLIS_SEC = 100;

  public ProduceRandomIntAction(DataBuffer<Integer> dataBuffer) {
    this(dataBuffer, DEFAULT_PRODUCE_DURATION_MILLIS_SEC);
  }

  public ProduceRandomIntAction(DataBuffer<Integer> dataBuffer, int produceDuration) {
    this.dataBuffer = dataBuffer;
    random = new Random();
    this.produceDuration = produceDuration;
  }

  @Override
  public Integer call() throws Exception {
    Integer newData = random.nextInt((int)System.currentTimeMillis());

    try {
      Thread.sleep(produceDuration);
      dataBuffer.put(newData);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return newData;
  }
}
