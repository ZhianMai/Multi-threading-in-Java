package johnston.thread.thread_safety_and_locking;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exclusively locking critical section can avoid data racing, and comes with performance overhead.
 * So minimizing the critical section execution code and using more flexible locking strategy can
 * help to improve performance.
 *
 * This demo compares performance on two different strategies of locking: using one lock for all
 * synced variable, and each variable uses one specific lock.
 */
public class LockStrategyComparison {
  private static final int INCREMENT_TIMES = 1000000;
  private static final int DEFAULT_THREAD_CORD_AMOUNT;
  private static final Random random = new Random(System.currentTimeMillis());

  static {
    DEFAULT_THREAD_CORD_AMOUNT = Runtime.getRuntime().availableProcessors();
  }

  static class IncrementalTaskThread extends Thread  {
    int[] taskList;
    AtomicInteger taskIdDistribute;
    boolean isBigLock;
    Object[] locks;
    CountDownLatch counter;

    public IncrementalTaskThread(int threadAmount, CountDownLatch counter,  boolean isBigLock) {
      taskList = new int[threadAmount];
      taskIdDistribute = new AtomicInteger(threadAmount);
      this.isBigLock = isBigLock;
      this.counter = counter;

      if (!isBigLock) {
        locks = new Object[threadAmount];

        for (int i = 0; i < threadAmount; i++) {
          locks[i] = new Object();
        }
      }
    }

    private synchronized void bigLockIncrement(int[] taskList, int idx) {
      taskList[idx]++;

      while (random.nextInt(100) != 0) {
      }
    }

    private void smallLockIncrement(Object lock, int[] taskList, int idx) {
      synchronized (lock) {
        taskList[idx]++;

        while (random.nextInt(5) != 0) {
        }
      }
    }

    @Override
    public void run() {
      int id = taskIdDistribute.decrementAndGet();

      if (id < 0) {
        System.out.println("No more task");
        return;
      }

      for (int i = 0; i < INCREMENT_TIMES; i++) {
        if (isBigLock) {
          bigLockIncrement(taskList, id);
        } else {
          smallLockIncrement(locks[id], taskList, id);
        }
      }
      counter.countDown();
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

    CountDownLatch counter = new CountDownLatch(DEFAULT_THREAD_CORD_AMOUNT);
    Thread bigLockThread = new IncrementalTaskThread(DEFAULT_THREAD_CORD_AMOUNT, counter, true);
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < DEFAULT_THREAD_CORD_AMOUNT; i++) {
      threadPool.execute(bigLockThread);
    }

    counter.await();
    long totalTime = (System.currentTimeMillis() - startTime);
    System.out.println("Big lock thread runtime: " + totalTime / 1000 + "sec.");

    counter = new CountDownLatch(DEFAULT_THREAD_CORD_AMOUNT);
    bigLockThread = new IncrementalTaskThread(DEFAULT_THREAD_CORD_AMOUNT, counter, false);
    startTime = System.currentTimeMillis();

    for (int i = 0; i < DEFAULT_THREAD_CORD_AMOUNT; i++) {
      threadPool.execute(bigLockThread);
    }

    counter.await();
    totalTime = (System.currentTimeMillis() - startTime);
    System.out.println("Small lock thread runtime: " + totalTime / 1000 + "sec.");
    threadPool.shutdown();
  }
}
