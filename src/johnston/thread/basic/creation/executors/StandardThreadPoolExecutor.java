package johnston.thread.basic.creation.executors;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * In general, using Executors factory to create thread pool is not allowed in large-scale
 * development. The standard method is to use standard ThreadPoolExecutor, although Executors
 * factory invokes ThreadPoolExecutor.
 *
 * Important ThreadPoolExecutor constructor parameter
 * corePoolSize: the minimum amount of core threads;
 * maximumPoolSize: the maximum amount of threads in the pool.
 * keepAliveTime: the maximum duration of a non-core threads being idle.
 * threadFactory: creations of new thread.
 * BlockingQueue<Runnable>: queue to hold tasks when no idle core threads available.
 * RejectedExecutionHandler: ways to handle new tasks when the pool is full, like throwing
 *   exception, discard, or replacing the oldest blocking task.
 *
 * Thread pool task scheduling policy when getting a new task:
 *  - If the number current core threads is < corePoolSize, new a new thread for this task, even
 *    though some core threads are idle;
 *  - If the number current core threads is >= corePoolSize...
 *    - Find if there are any idle core threads, then replace it;
 *    - else if no idle core threads and the blocking queue is not full, enqueue;
 *    - else if the blocking queue is full, new a new thread until the total number of threads
 *      > maximumPoolSize
 *    - else execute rejected execution policy.
 *
 * This demo shows how threadPoolExecutor scheduling tasks with different thread pool parameters
 * and different reject task policies.
 */
public class StandardThreadPoolExecutor {
  // Modify these three params to tune different thread pools.
  private static final int DEFAULT_CORE_POOL_SIZE = 1;
  private static final int DEFAULT_MAXIMUM_POOL_SIZE = 2;
  private static final int DEFAULT_BLOCKING_QUEUE_SIZE = 20;

  private static final int DEFAULT_KEEP_ALIVE_MILLIS_SECOND = 100;
  private static final int DEFAULT_TASK_AMOUNT = 100;
  private static final int DEFAULT_TASK_SLEEP_MILLIS_SECOND = 10;

  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
    }

    public void run() {
      System.out.println(this.getName() + " is running.");
      try {
        Thread.sleep(DEFAULT_TASK_SLEEP_MILLIS_SECOND);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println(this.getName()  + " finished running.");
    }
  }

  public static void main(String[] args) {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
        DEFAULT_CORE_POOL_SIZE,
        DEFAULT_MAXIMUM_POOL_SIZE,
        DEFAULT_KEEP_ALIVE_MILLIS_SECOND,
        TimeUnit.DAYS,
        new LinkedBlockingDeque<>(DEFAULT_BLOCKING_QUEUE_SIZE),
        // Try different reject handling policy!
        // new ThreadPoolExecutor.DiscardOldestPolicy() // Dequeue & discard one task to make room
        // new ThreadPoolExecutor.AbortPolicy() // Throw RejectedExecutionException
        // new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
        new ThreadPoolExecutor.DiscardPolicy() // Silent AbortPolicy()
    );

    for (int i = 0; i < DEFAULT_TASK_AMOUNT; i++) {
      executor.execute(new ThreadDemo(String.valueOf(i)));
    }

    executor.shutdown();
  }
}
