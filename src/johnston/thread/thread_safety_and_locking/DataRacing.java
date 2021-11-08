package johnston.thread.thread_safety_and_locking;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Data racing causes thread unsafely.
 */
public class DataRacing {
  private static final int DEFAULT_INCREMENT_TIMES = 10000;
  private static final int DEFAULT_THREAD_AMOUNT = 10;
  private static int unsafeInt = 0;

  static class UnsafeThread extends Thread {
    public UnsafeThread(String name) {
      super(name);
    }

    public void run() {
      for (int i = 0; i < DEFAULT_INCREMENT_TIMES; i++) {
        unsafeInt++;
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
        DEFAULT_THREAD_AMOUNT,
        DEFAULT_THREAD_AMOUNT,
        1,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(DEFAULT_THREAD_AMOUNT),
        new ThreadPoolExecutor.DiscardPolicy()
    );

    Thread unsafeThread = new UnsafeThread("Unsafe Thread");

    for (int i = 0; i < DEFAULT_THREAD_AMOUNT; i++) {
      executor.execute(unsafeThread);
    }

    executor.shutdown();

    if (executor.awaitTermination(1, TimeUnit.MINUTES)) {
      System.out.println("The expected value is: " +
          (DEFAULT_THREAD_AMOUNT * DEFAULT_INCREMENT_TIMES));
      System.out.println("The unsafe thread's result is:" + unsafeInt);
    }
  }
}
