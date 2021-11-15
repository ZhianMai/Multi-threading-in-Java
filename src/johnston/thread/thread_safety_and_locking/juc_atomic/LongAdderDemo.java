package johnston.thread.thread_safety_and_locking.juc_atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * AtomicInteger can have hotspot problem when too many threads competing each other to get it.
 * Since AtomicInteger uses lightweight spinning lock to block waiting threads, so when too many
 * waiting threads it will consume a lot of CPU resource to run the spinning lock.
 *
 * One of the solution is to use <i>LongAdder</i>. LongAdder is like ThreadLocal which assign a
 * separate variable to each thread. It returns the sum of all separated variable when calling
 * sum() method. It greatly avoids hotspot problem on AtomicInteger and maintains Atomicity.
 *
 * This demo shows that LongAdder has much better performance than AtomicInteger.
 */
public class LongAdderDemo {
  private static final int THREAD_INCREMENT_TIMES = 100000000;
  private static final int THREAD_AMOUNT = 10;
  private static LongAdder longAdderIncrement = new LongAdder();
  private static AtomicInteger atomicIncrement = new AtomicInteger(0);

  public static void main(String[] args) {
    CountDownLatch countDownLatch = new CountDownLatch(THREAD_AMOUNT);

    Runnable incrementTask = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < THREAD_INCREMENT_TIMES; i++) {
          longAdderIncrement.add(1);
          /**
           * Comparison
           */
          // atomicIncrement.incrementAndGet();
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

    long timeBegin = System.currentTimeMillis();
    for (int i = 0; i < THREAD_AMOUNT; i++) {
      threadPool.execute(incrementTask);
    }

    threadPool.shutdown();
    try {
      countDownLatch.await(); // Print the result after all threads finished.
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Expected result: " + (THREAD_INCREMENT_TIMES * THREAD_AMOUNT));
    // System.out.println("Actual result:   " + longAdderIncrement.sum());
    System.out.println("Actual result:   " + atomicIncrement.get());
    System.out.println("Time taken: " + (System.currentTimeMillis() - timeBegin) + " ms");
  }
}
