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

  public Producer(String name, Callable action) {
    NAME = name;
    this.action = action;
  }

  @Override
  public void run() {
    while (true) {
      try {
        Object output = action.call();

        if (output != null) {
          System.out.println("Producer " + TURN_COUNTER.get() + "-th time produced.");
          TURN_COUNTER.incrementAndGet();
        }

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
