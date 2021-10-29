package johnston.thread.basic.creation.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Cached thread pool has mutable size. Each new coming task will be allocated a new thread. The
 * size of cached thread pool is unbounded until reaches the limit that JVM allows.
 *
 * A thread whose idle time is over 60s will be terminated.
 */
public class ExecutorCachedThreadPool {
  private static final int SLEEP_MILLIS_SEC = 1000;
  private static final int THREAD_POOL_SIZE = 3;
  private static final int THREAD_AMOUNT = 10;

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
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    Thread thread = new ThreadDemo("Thread_A");

    for (int i = 0; i < THREAD_AMOUNT; i++) {
      thread.setName(thread.getName() + i);
      cachedThreadPool.submit(thread);
    }

    System.out.println("\nMain thread finished thread pool loading.\n");
    cachedThreadPool.shutdown();
  }
}
