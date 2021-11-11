package johnston.thread.thread_safety_and_locking.consumer_producer.components.consumer;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * General consumer class. Get data from consume action (Callable class) and use it to do some
 * work. Need to specify how much time needed to consume the fetched data.
 */
public class Consumer implements Runnable {
  public static final int DEFAULT_CONSUME_DURATION_MILLIS_SEC = 100;
  private static final AtomicInteger TURN_COUNTER = new AtomicInteger(0);
  private final String NAME;
  private Callable action;
  private int consumeDuration;

  public Consumer(String name, Callable action) {
    this(name, action, DEFAULT_CONSUME_DURATION_MILLIS_SEC);
  }

  public Consumer(String name, Callable action, int consumeDuration) {
    NAME = name;
    this.action = action;
    this.consumeDuration = consumeDuration;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Object output = action.call();

        if (output != null) {
          System.out.println(NAME + " " + TURN_COUNTER.get() + "-th time consumed: " + output);
          // Do some work if got data from Callable action
          Thread.sleep(consumeDuration);
          TURN_COUNTER.incrementAndGet();
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
