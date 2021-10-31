package johnston.thread.basic.properties.executors;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolExecutor has three hood methods:
 *  - void beforeExecute(Thread t, Runnable target): this method runs before each task begins.
 *  - afterExecute(Runnable target, Throwable t): this method runs after each task begins.
 *  - terminated(): this method runs when the pool is shutdown.
 *
 *  These three methods can be overridden to do some tasks, like customize environment, clean up
 *  data, etc.
 *
 *  This demo overrides these three methods to record the total runtime of all Runnable tasks. The
 *  task will sleep random milli sec.
 */
public class ThreadPoolHookMethods {
  private static final int DEFAULT_CORE_POOL_SIZE = 2;
  private static final int DEFAULT_MAX_POOL_SIZE = 4;
  private static final int DEFAULT_BLOCKING_QUEUE_SIZE = 50;
  private static final int DEFAULT_THREAD_KEEP_ALIVE_MILLIS_SEC = 50;
  private static final int SLEEP_MILLIS_SEC = 50;
  private static final int DEFAULT_TASK_AMOUNT = 20;

  private static Map<Runnable, Long> threadStartTime;
  private static ExecutorService threadPool;
  private static Random rand;
  private static long totalRuntimeMillisSec = 0;

  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
    }

    public void run() {
      try {
        // Sleep random milli sec
        Thread.sleep(rand.nextInt(SLEEP_MILLIS_SEC) + 1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private static void init() {
    threadStartTime = new HashMap<>();
    rand = new Random();

    threadPool = new ThreadPoolExecutor(
        DEFAULT_CORE_POOL_SIZE,
        DEFAULT_MAX_POOL_SIZE,
        DEFAULT_THREAD_KEEP_ALIVE_MILLIS_SEC,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(DEFAULT_BLOCKING_QUEUE_SIZE)) {
            @Override
            protected void beforeExecute(Thread t, Runnable target) {
              String threadName = t.getName();
              threadStartTime.put(target, System.currentTimeMillis());
              System.out.println(threadName + " starts running");
            }

            @Override
            protected void afterExecute(Runnable target, Throwable t) {
              long endTime = System.currentTimeMillis();
              long runtime = endTime - threadStartTime.get(target);
              System.out.println("Thread runtime was " + runtime);

              totalRuntimeMillisSec += runtime;
            }

            @Override
            protected void terminated() {
              System.out.println("Total running time is: " + totalRuntimeMillisSec);
            }
    };
  }

  public static void main(String[] args) {
    init();
    Thread threadDemo = new ThreadDemo("Demo thread");

    for (int i = 0; i < DEFAULT_TASK_AMOUNT; i++) {
      threadPool.submit(threadDemo);
    }

    threadPool.shutdown();
  }
}

