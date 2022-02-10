package johnston.thread.thread_safety_and_locking;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Semaphore is a shared lock that holds a set of permit. Threads which can acquire one or more
 * permits from semaphore can enter the critical section guarded by semaphore. Threads also can
 * release one or more permits to semaphore.
 *
 * Semaphore is not a mutual exclusive lock. If one malicious thread wants to enter the critical
 * section guarded by semaphore, it can just release a permit for itself!
 *
 * In this demo the number of permit in semaphore is 2, so in each period (five seconds) only two
 * tasks are running.
 */
public class SemaphoreDemo {
  private static final int DEFAULT_THROUGHPUT = 2;
  private static final int DEFAULT_TASK_DURATION_MILLIS_SEC = 5000;
  private static final int DEFAULT_CORE_POOL_SIZE = 10;
  private static final int TASK_AMOUNT = 50;

  public static void main(String[] args) {
    final Semaphore SEMAPHORE = new Semaphore(DEFAULT_THROUGHPUT);
    AtomicInteger index = new AtomicInteger(0);
    Runnable task = () -> {
      try {
        SEMAPHORE.acquire();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Running task " + index.getAndIncrement() + " @ " + dtf.format(now));
        Thread.sleep(DEFAULT_TASK_DURATION_MILLIS_SEC);
        SEMAPHORE.release();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    };

    ThreadPoolExecutor executor = new ThreadPoolExecutor(
        DEFAULT_CORE_POOL_SIZE,
        DEFAULT_CORE_POOL_SIZE,
        DEFAULT_TASK_DURATION_MILLIS_SEC,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(DEFAULT_CORE_POOL_SIZE),
        new ThreadPoolExecutor.DiscardPolicy() // Silent AbortPolicy()
    );

    for (int i = 0; i < TASK_AMOUNT; i++) {
      executor.execute(task);
    }
    executor.shutdown();
  }
}
