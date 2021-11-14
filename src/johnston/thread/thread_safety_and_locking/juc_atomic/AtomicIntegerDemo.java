package johnston.thread.thread_safety_and_locking.juc_atomic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * There are three primitive types in JUC atomic package: int, long, and boolean. They ensure
 * operations like increment, decrement, set are all atomic, so it's thread safe.
 *
 * They use lightweight lock to ensure thread safety. Lightweight lock uses spin lock to block
 * the waiting thread, and because integer operation is not time-consuming, so it it's much
 * more faster than using heavyweight lock. Heavyweight lock needs to switch to the OS kernel mode
 * to ensure thread safety, so try to avoid using synchronized keyword to guard primitive type's
 * thread-safety.
 *
 * This demo shows that AtomicInteger is thread-safety.
 */
public class AtomicIntegerDemo {
  private static final int THREAD_INCREMENT_TIMES = 1000000;
  private static final int THREAD_AMOUNT = 10;
  private static AtomicInteger atomicIncrement = new AtomicInteger(0);

  public static void main(String[] args) {
    CountDownLatch countDownLatch = new CountDownLatch(THREAD_AMOUNT);

    Runnable incrementTask = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < THREAD_INCREMENT_TIMES; i++) {
          atomicIncrement.incrementAndGet();
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
    System.out.println("Actual result:   " + atomicIncrement.get());
  }
}
