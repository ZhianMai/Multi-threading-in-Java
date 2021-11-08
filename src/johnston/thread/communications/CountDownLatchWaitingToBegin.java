package johnston.thread.communications;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a different usage of CountDownLatch. Instead of letting the calling wait() thread to
 * wait until decrement to 0, let all threads starts at the same time by calling await()!
 */
public class CountDownLatchWaitingToBegin {
  private static final int DEFAULT_MAX_REST_MILLIS_SEC = 10000;
  private static final int DEFAULT_THREAD_AMOUNT = 10;
  private static CountDownLatch counter;
  private static Random random = new Random();

  static class Runner implements Runnable {
    public void run() {
      try {
        Thread.sleep(random.nextInt(DEFAULT_MAX_REST_MILLIS_SEC));
        counter.countDown();
        counter.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Everyone is awake! Start running!");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    counter = new CountDownLatch(DEFAULT_THREAD_AMOUNT);
    Runnable sleepThread = new Runner();

    for (int i = 0; i < DEFAULT_THREAD_AMOUNT; i++) {
      new Thread(sleepThread).start();
    }
  }
}
