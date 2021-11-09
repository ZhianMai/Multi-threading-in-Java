package johnston.thread.thread_safety_and_locking;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Using synchronized keyword to modify static method is different to modify non-static method.
 * Synchronized non-static method is object-level lock, while synchronized static method is
 * class-level lock.
 *
 * This demo shows that threads from two different objects can access a synchronized non-static
 * method at the same time, while synchronized static method allows only one thread entered at
 * the same time.
 */
public class StaticMethodLock {
  private static final int INCREMENT_TIMES = 100;
  private static final int DEFAULT_THREAD_CORD_AMOUNT;

  static {
    DEFAULT_THREAD_CORD_AMOUNT = Runtime.getRuntime().availableProcessors() / 2;
  }

  static class StaticLockThread extends Thread  {
    int[] taskList;
    AtomicInteger taskIdDistribute;
    boolean isStatic;
    CountDownLatch counter;

    public StaticLockThread(int threadAmount, CountDownLatch counter, boolean isStatic) {
      taskList = new int[threadAmount];
      taskIdDistribute = new AtomicInteger(threadAmount);
      this.isStatic = isStatic;
      this.counter = counter;
    }

    private static synchronized void staticSyncIncrement(int[] taskList, int idx)
        throws InterruptedException {
      taskList[idx]++;
      Thread.sleep(10); // Simulates time-consuming work
    }

    private synchronized void objectSyncIncrement(int[] taskList, int idx)
        throws InterruptedException {
      taskList[idx]++;
      Thread.sleep(10); // Simulates time-consuming work
    }

    @Override
    public void run() {
      int id = taskIdDistribute.decrementAndGet();

      if (id < 0) {
        System.out.println("No more task");
        return;
      }

      for (int i = 0; i < INCREMENT_TIMES; i++) {
        if (isStatic) {
          try {
            staticSyncIncrement(taskList, id);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        } else {
          try {
            objectSyncIncrement(taskList, id);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
      counter.countDown();
      System.out.print(taskList[id] + ", ");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        DEFAULT_THREAD_CORD_AMOUNT,
        DEFAULT_THREAD_CORD_AMOUNT + 1,
        10,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(DEFAULT_THREAD_CORD_AMOUNT),
        new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
    );

    CountDownLatch counter = new CountDownLatch(DEFAULT_THREAD_CORD_AMOUNT * 2);
    Thread testThreadA =
        new StaticLockThread(DEFAULT_THREAD_CORD_AMOUNT, counter, true);
    Thread testThreadB =
        new StaticLockThread(DEFAULT_THREAD_CORD_AMOUNT, counter, true);
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < DEFAULT_THREAD_CORD_AMOUNT; i++) {
      threadPool.execute(testThreadA);
      threadPool.execute(testThreadB);
    }

    counter.await();
    long totalTime = (System.currentTimeMillis() - startTime);
    System.out.println("\nStatic lock thread runtime: " + totalTime + " milli sec.\n");

    counter = new CountDownLatch(DEFAULT_THREAD_CORD_AMOUNT * 2);
    testThreadA = new StaticLockThread(DEFAULT_THREAD_CORD_AMOUNT, counter, false);
    testThreadB = new StaticLockThread(DEFAULT_THREAD_CORD_AMOUNT, counter, false);
    startTime = System.currentTimeMillis();

    for (int i = 0; i < DEFAULT_THREAD_CORD_AMOUNT; i++) {
      threadPool.execute(testThreadA);
      threadPool.execute(testThreadB);
    }

    counter.await();
    totalTime = (System.currentTimeMillis() - startTime);
    System.out.println("\nObject lock thread runtime: " + totalTime + " milli sec.");
    threadPool.shutdown();

    // The runtime of using static lock thread should be 2 times longer than using object lock
    // thread.
  }
}
