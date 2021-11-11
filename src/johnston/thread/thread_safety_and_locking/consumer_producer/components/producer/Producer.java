package johnston.thread.thread_safety_and_locking.consumer_producer.components.producer;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * General producer class. Get data from produce action (Callable class) and use it to do some
 * work.
 */
public class Producer implements Runnable {
  private static final AtomicInteger TURN_COUNTER = new AtomicInteger(0);
  private final String NAME;
  private Callable action;
  private int produceDuration;
  public static final int DEFAULT_PRODUCE_DURATION_MILLIS_SEC = 100;
  private int maxProduced;

  public Producer(String name, Callable action) {
    this(name, action, DEFAULT_PRODUCE_DURATION_MILLIS_SEC, Integer.MAX_VALUE);
  }

  public Producer(String name, Callable action, int produceDuration) {
    this(name, action, produceDuration, Integer.MAX_VALUE);
  }

  public Producer(String name, Callable action, int produceDuration, int maxProduced) {
    NAME = name;
    this.action = action;
    this.produceDuration = produceDuration;
    this.maxProduced = maxProduced;
  }

  @Override
  public void run() {
    while (TURN_COUNTER.get() < maxProduced) {
      try {
        Object output = action.call();

        if (output != null) {
          Thread.sleep(produceDuration);
          System.out.println(NAME + " " + TURN_COUNTER.get() + "-th time produced: " + output);
          TURN_COUNTER.incrementAndGet();
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
