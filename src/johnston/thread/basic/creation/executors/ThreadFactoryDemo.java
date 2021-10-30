package johnston.thread.basic.creation.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <i>ThreadFactory</i> is an interface from concurrent package. It has only one
 * method: newThread(Runnable r). User can implement this interface to customize the method of
 * creating new threads, and pass this interface instance to a thread pool. The thread pool then
 * will use this thread factory to create reusable new threads.
 *
 * In this demo, an implementation of ThreadFactory is created and pass it to the thread pool.
 * When the thread pool receives new Runnable tasks, it will use the threads created by
 * ThreadFactory to run it.
 */
public class ThreadFactoryDemo {
  private static final int DEFAULT_THREAD_AMOUNT = 10;
  private static final int DEFAULT_THREAD_POOL_SIZE = 3;

  static class ThreadFactoryImpl implements ThreadFactory {
    static AtomicInteger threadID = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable target) {
      String threadName = "thread_" +  String.valueOf(threadID.getAndIncrement()) +
          " named by ThreadFactory";
      Thread thread = new Thread(target, threadName);

      return thread;
    }
  }

  public static void main(String[] args) {
    ExecutorService singlePool = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE,
        new ThreadFactoryImpl());

    for (int i = 0; i < DEFAULT_THREAD_AMOUNT; i++) {
      int taskID = i;

      singlePool.submit(new Runnable() {
        @Override
        public void run() {
          System.out.println(Thread.currentThread().getName() + " is running task-" + taskID);
          try {
            Thread.sleep(10);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      });
    }

    singlePool.shutdown();
  }
}
