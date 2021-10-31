package johnston.thread.basic.properties.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A thread pool can run forever if not calling shutdown() method, which will prevent the main
 * thread from terminating.
 *
 * When shutdown() is called, the thread pool will no longer receive new thread task, and waits
 * for all threads in queue are executed, then exits.
 *
 * Adding new threads on a shutdown thread pool would throw RejectedExecutionException.
 *
 * In this demo, four tasks are added to the thread pool then shut it down. Four tasks will be
 * finished after shutting down the thread pool.
 */
public class ThreadPoolShutdown {
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
    singlePool.submit(threadA);
    singlePool.submit(threadB);

    System.out.println("\n*** Thread pool shutdown! ***\n");
    singlePool.shutdown();

    // RejectedExecutionException
    //singlePool.execute(threadA);
  }
}
