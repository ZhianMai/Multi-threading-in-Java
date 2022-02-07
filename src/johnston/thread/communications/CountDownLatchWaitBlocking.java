package johnston.thread.communications;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CountDownLatch is a decremental counter for multi-threading. It inits as an integer, and any
 * threads can call countDown() to make is decrement one time. Threads calling CountDownLatch::wait
 * will be blocked until the counter is 0. It's like a join() method that can specify the location
 * of exit-joining point instead of waiting the joined thread terminated.
 */
public class CountDownLatchWaitBlocking {
  private static final int DEFAULT_SLEEP_MILLIS_SEC = 100;
  private static final int DEFAULT_THREAD_AMOUNT = 10;
  private static CountDownLatch counter;
  private static AtomicInteger atomicCounter;

  static class SleepThread implements Runnable {

    public void run() {
      // latch.await();
      try {
        Thread.sleep(DEFAULT_SLEEP_MILLIS_SEC);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      atomicCounter.incrementAndGet();
      counter.countDown();
      System.out.println("Countdown--");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    // Count down 10 times
    counter = new CountDownLatch(DEFAULT_THREAD_AMOUNT);
    Runnable sleepThread = new SleepThread();
    atomicCounter = new AtomicInteger(0);

    for (int i = 0; i < DEFAULT_THREAD_AMOUNT; i++) {
      new Thread(sleepThread).start();
    }

    counter.await(); // Main thread waits until counter == 0
    System.out.println("The result is: " + atomicCounter);
  }
}
