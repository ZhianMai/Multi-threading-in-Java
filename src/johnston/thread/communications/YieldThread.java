package johnston.thread.communications;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Method yield() is provided by Thread class. When a thread calls Thread.yield(), it gives up
 * the usage of CPU and put itself into the thread scheduling, which depends on the thread
 * priority. The state is still Runnable, but it's "ready to run" instead of "running".
 *
 * In this demo, an array of low priority thread and an array of high priority thread yield to
 * each other, and the final result shows that high priority threads have more execution times.
 */
public class YieldThread {
  private static final int THREAD_AMOUNT = 10;
  private static final int RUN_ROUNDS = 100000;
  public static AtomicInteger runningRoundRemains;

  static class ThreadDemo extends Thread {
    private int[] executeCount;

    public ThreadDemo(String name, int[] executeCount) {
      super(name);
      this.executeCount = executeCount;
    }

    public void run() {
      while (runningRoundRemains.decrementAndGet() > 0) {
        executeCount[0]++;
        Thread.yield();
      }

      System.out.println(this.getName() + " has run " + executeCount[0] + " times.");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    int[][] lowPriorityRunCount = new int[THREAD_AMOUNT][1];
    int[][] highPriorityRunCount = new int[THREAD_AMOUNT][1];
    Thread[] lowPriorityThread = new Thread[THREAD_AMOUNT];
    Thread[] highPriorityThread = new Thread[THREAD_AMOUNT];
    runningRoundRemains = new AtomicInteger(RUN_ROUNDS); ;

    for (int i = 0; i < THREAD_AMOUNT; i++) {
      lowPriorityThread[i] = new ThreadDemo("LowPriorityThread_" + i, lowPriorityRunCount[i]);
      highPriorityThread[i] = new ThreadDemo("HighPriorityThread_" + i, highPriorityRunCount[i]);

      lowPriorityThread[i].setPriority(Thread.MIN_PRIORITY);
      highPriorityThread[i].setPriority(Thread.MAX_PRIORITY);
    }

    for (int i = 0; i < THREAD_AMOUNT; i++) {
      lowPriorityThread[i].start();
      highPriorityThread[i].start();
    }

    Thread.sleep(1000);

    int lowPrioritySum = 0;
    int highPrioritySum = 0;

    for (int[] i : lowPriorityRunCount) {
      lowPrioritySum += i[0];
    }

    for (int[] i : highPriorityRunCount) {
      highPrioritySum += i[0];
    }

    System.out.println("\nLow priority thread has run " + lowPrioritySum + " times.");
    System.out.println("High priority thread has run " + highPrioritySum + " times.");
  }
}
