package johnston.thread.thread_safety_and_locking.juc_atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Atomic array has three components: integer, long, and reference array. They guarantee each
 * element in the array is thread-safety.
 */
public class AtomicArrayDemo {
  private static final int THREAD_INCREMENT_TIMES = 1000000;
  private static final int THREAD_AMOUNT = 10;
  private static final int ARRAY_LENGTH = 10;
  private static AtomicIntegerArray atomicArrayIncrement = new AtomicIntegerArray(ARRAY_LENGTH);

  public static void main(String[] args) {
    CountDownLatch countDownLatch = new CountDownLatch(THREAD_AMOUNT);

    Runnable incrementTask = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < THREAD_INCREMENT_TIMES; i++) {
          for (int j = 0; j < ARRAY_LENGTH; j++) {
            atomicArrayIncrement.incrementAndGet(j);
          }
        }
        countDownLatch.countDown();
      }
    };

    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        THREAD_AMOUNT,
        THREAD_AMOUNT * 2,
        10,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(THREAD_AMOUNT),
        new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
    );

    for (int i = 0; i < THREAD_AMOUNT; i++) {
      threadPool.execute(incrementTask);
    }

    threadPool.shutdown();
    try {
      countDownLatch.await(); // Print the result after all threads finished.
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Expected result of each element: " +
        (THREAD_INCREMENT_TIMES * THREAD_AMOUNT));
    System.out.print("Actual result of each element: ");

    for (int i = 0; i < ARRAY_LENGTH; i++) {
      System.out.print(atomicArrayIncrement.get(i) + ",");
    }
  }
}
