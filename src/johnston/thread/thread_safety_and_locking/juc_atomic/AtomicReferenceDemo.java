package johnston.thread.thread_safety_and_locking.juc_atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

/**
 * There are three reference types in JUC atomic package: reference, stamped reference, and marked
 * reference. AtomicReference can ensure that referencing the object can always be atomic. The
 * AtomicStampedReference is like adding an integer as version or mark on the object like
 * <Object, Integer>. The AtomicMakrkableReference is like <Object, Boolean>.
 *
 * Be careful! Modifying the object referenced by AtomicReference is not atomic!
 */
public class AtomicReferenceDemo {
  private static final int THREAD_INCREMENT_TIMES = 1000000;
  private static final int THREAD_AMOUNT = 10;
  private static AtomicReference<OneInteger> atomicRef;
  private static AtomicInteger increment = new AtomicInteger(0);

  static class OneInteger {
    public int increment;

    public OneInteger(int increment) {
      this.increment = increment;
    }

    public OneInteger increment() {
      increment++;
      return this;
    }
  }

  public static void main(String[] args) {
    CountDownLatch countDownLatch = new CountDownLatch(THREAD_AMOUNT);
    OneInteger atomicInt = new OneInteger(0);
    atomicRef = new AtomicReference(atomicInt);

    Runnable incrementTask = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < THREAD_INCREMENT_TIMES; i++) {
          atomicRef.getAndSet(new OneInteger(increment.incrementAndGet()));
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

    System.out.println("Expected result: " + (THREAD_INCREMENT_TIMES * THREAD_AMOUNT));
    System.out.println("Actual atomic ref result:   " + atomicRef.get().increment);
  }
}
