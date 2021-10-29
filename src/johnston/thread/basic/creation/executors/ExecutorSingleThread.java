package johnston.thread.basic.creation.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Executor can create a thread pool with single thread. The execution order is guaranteed FIFO.
 *
 * When using Thread class to run a thread, it's one time usage, but a thread pool can reuse
 * its thread slot.
 *
 * In this Demo, a single thread pool is loaded multiple threads, and these threads will be
 * executed in FIFO order.
 */
public class ExecutorSingleThread {
  private static final int SLEEP_MILLIS_SEC = 1000;

  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
      System.out.println("Thread named " + this.getName() + " created.");
      System.out.println("Created by : " + Thread.currentThread().getName() + " thread.\n");
    }

    public void run() {
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Thread named \"" + this.getName() + "\" is running.");

      try {
        Thread.sleep(SLEEP_MILLIS_SEC);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Thread named \"" + this.getName() + "\" is finished.");
    }
  }

  public static void main(String[] args) {
    ExecutorService singlePool = Executors.newSingleThreadExecutor();
    Runnable threadA = new ThreadDemo("Thread_A");
    Runnable threadB = new ThreadDemo("Thread_B");

    singlePool.execute(threadA);
    singlePool.execute(threadB);
    // It can run a same Runnable object multiple times.
    singlePool.submit(threadA);
    singlePool.submit(threadB);

    System.out.println("\nMain thread finished thread loading.\n");
    singlePool.shutdown();
  }
}
