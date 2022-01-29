package johnston.thread.basic.properties.executors;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * If shutdownNow() is called, the thread pool will terminates all threads that are currently
 * running, and return the tasks that are not yet started.
 */
public class ThreadPoolShutdownNow {
  private static final int SLEEP_MILLIS_SEC = 1000;

  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
      System.out.println("Thread named " + this.getName() + " created.");
      System.out.println("Created by : " + Thread.currentThread().getName() + " thread.\n");
    }

    public void run() {
      System.out.println("Thread named \"" + this.getName() + "\" is running.");

//      try {
//        Thread.sleep(SLEEP_MILLIS_SEC);
//      } catch (InterruptedException e) {
//        System.out.println(this.getName() + " is waken!");
//        // e.printStackTrace();
//      }

      while(true) {
        if (Thread.currentThread().isInterrupted()) {
          System.out.println("我命由天不由我啊！");
          break;
        }
      }

      // System.out.println("Thread named \"" + this.getName() + "\" is finished.");
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

    System.out.println("\n*** Thread pool shutdown now! ***\n");
    List<Runnable> unStartedThreadList =  singlePool.shutdownNow();

    System.out.println("Unstated thread amount: " + unStartedThreadList.size());
  }
}
