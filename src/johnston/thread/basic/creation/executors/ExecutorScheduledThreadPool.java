package johnston.thread.basic.creation.executors;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled thread pool supports repeatedly running a thread task with fixed time of period.
 * Thread pool will run the scheduled task repeatedly until calling shutdown() method.
 *
 * scheduledThreadPool has two types of schedule: fixed period or fixed delay.
 *
 * For fixed period, the period is minimal runtime of one task. If task A1 runs longer than
 * the period, than it won't start task A2 until A1 finishes. If task A1 runs faster than the
 * period, A2 won't start but wait until the minimal interval time is satisfied.
 *
 * For fixed delay, the delay is the fixed interval between two tasks. If task A1 finishes, it
 * will wait the delay time than start task A2.
 *
 * Beside scheduled thread pool, Executors also provides single thread scheduled executor.
 *
 * In this demo, a set of threads with different scheduled periods are loaded into to thread pool,
 * and let the thread pool run for a while. The result shows that threads with smaller scheduled
 * period run more times than others. Fixed delay demo skips because it's very similar to this one.
 */
public class ExecutorScheduledThreadPool {
  private static final int EXECUTION_INTERVAL_MILLIS_SECOND = 50;
  private static final int THREAD_POOL_SIZE = 3;
  private static final int THREAD_AMOUNT = 10;
  private static final int MAIN_THREAD_WAIT_MILLIS_SECOND = 100;

  static class ThreadDemo extends Thread {
    private int[] runCount;

    public ThreadDemo(String name, int[] runCount) {
      super(name);
      this.runCount = runCount;
      System.out.println("Thread named " + this.getName() + " created.");
    }

    public void run() {
      runCount[0]++;
    }
  }

  public static void main(String[] args) throws InterruptedException {
    int[][] threadRunCount = new int[THREAD_AMOUNT][1];
    ScheduledExecutorService scheduledThreadPool =
        Executors.newScheduledThreadPool(THREAD_POOL_SIZE);

    for (int i = 0; i < THREAD_AMOUNT; i++) {
      // Demo fixed rate method here only. To switch to fixed delay method, change the method name
      // to:
      // scheduledThreadPool.scheduleAtFixedDelay(...);
      scheduledThreadPool.scheduleAtFixedRate(new ThreadDemo("Thread " + i, threadRunCount[i]), 0,
          EXECUTION_INTERVAL_MILLIS_SECOND / (i + 1), TimeUnit.MILLISECONDS);
    }

    Thread.sleep(MAIN_THREAD_WAIT_MILLIS_SECOND);
    scheduledThreadPool.shutdown();

    System.out.println("\n");

    for (int i = 0; i < THREAD_AMOUNT; i++) {
      System.out.println("Thread " + i + " has run " + threadRunCount[i][0] + " times.");
    }
  }
}
