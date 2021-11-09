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
  private static final int INCREMENT_TIMES = 100;
  private static final int DEFAULT_THREAD_CORD_AMOUNT;

  static {
    DEFAULT_THREAD_CORD_AMOUNT = Runtime.getRuntime().availableProcessors() / 2;
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

    private synchronized void bigLockIncrement(int[] taskList, int idx) throws InterruptedException {
      taskList[idx]++;
      Thread.sleep(10);
    }

    private void smallLockIncrement(Object lock, int[] taskList, int idx) throws InterruptedException {
      synchronized (lock) {
        taskList[idx]++;
        Thread.sleep(10);
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
          try {
            bigLockIncrement(taskList, id);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        } else {
          try {
            smallLockIncrement(locks[id], taskList, id);
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

    CountDownLatch counter = new CountDownLatch(DEFAULT_THREAD_CORD_AMOUNT);
    Thread bigLockThread = new IncrementalTaskThread(DEFAULT_THREAD_CORD_AMOUNT, counter, true);
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < DEFAULT_THREAD_CORD_AMOUNT; i++) {
      threadPool.execute(bigLockThread);
    }

    counter.await();
    long totalTime = (System.currentTimeMillis() - startTime);
    System.out.println("Big lock thread runtime: " + totalTime + " milli sec.");

    counter = new CountDownLatch(DEFAULT_THREAD_CORD_AMOUNT);
    bigLockThread = new IncrementalTaskThread(DEFAULT_THREAD_CORD_AMOUNT, counter, false);
    startTime = System.currentTimeMillis();

    for (int i = 0; i < DEFAULT_THREAD_CORD_AMOUNT; i++) {
      threadPool.execute(bigLockThread);
    }

    counter.await();
    totalTime = (System.currentTimeMillis() - startTime);
    System.out.println("Small lock thread runtime: " + totalTime + " milli sec.");
    threadPool.shutdown();

    // The runtime of big lock thread should be [thread_core_amount] times longer than small lock
    // thread.
  }
}
