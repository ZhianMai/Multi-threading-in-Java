package johnston.thread.basic.creation.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread pool with fixed size n can allow at most n threads run at the same time. The order is
 * still FIFO.
 */
public class ExecutorFixedThreadPool {
  private static final int SLEEP_MILLIS_SEC = 1000;
  private static final int DEFAULT_THREAD_POOL_SIZE = 3;
  private static final int DEFAULT_THREAD_AMOUNT = 10;

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
    ExecutorService threadPool = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
    Thread thread = new ThreadDemo("Thread_A");

    for (int i = 0; i < DEFAULT_THREAD_AMOUNT; i++) {
      thread.setName(thread.getName() + i);
      threadPool.submit(thread);
    }

    System.out.println("\nMain thread finished thread pool loading.\n");
    threadPool.shutdown();
  }
}
