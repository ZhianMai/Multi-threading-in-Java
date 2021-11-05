package johnston.thread.basic.properties.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * General way to shutdown a thread pool:
 *  - Call shutdown() to stop accepting new task;
 *  - awaitTermination(long timeout, TimeUnit unit) to wait for the existing task to finish.
 *  - If timeout, call shutdownNow() to force all task finished.
 *  - Iteratively call shutdownNow() -- awaitTermination()
 */
public class ThreadPoolProperShutDown {
  private static final int SLEEP_TIME_MILLIS_SEC = 60;
  private static final int DEFAULT_SHUT_DOWN_AWAIT_TIME = 10;
  private static final int DEFAULT_SHUTDOWN_NOW_ATTEMPT_TIME = 100;

  private static void threadPoolShutDown(ExecutorService threadPool) {
    if (!(threadPool instanceof ExecutorService) || threadPool.isShutdown()) {
      return;
    }

    threadPool.shutdown();

    try {
      if (!threadPool.awaitTermination(DEFAULT_SHUT_DOWN_AWAIT_TIME, TimeUnit.MILLISECONDS)) {
        System.out.println("Time out 1st time, call shutDownNow()");
        threadPool.shutdownNow();
      }

      if (!threadPool.awaitTermination(DEFAULT_SHUT_DOWN_AWAIT_TIME, TimeUnit.MILLISECONDS)) {
        System.out.println("Time out 2nd time, Iteratively call shutdownNow--awaitTermination");
        threadPool.shutdownNow();

        for (int i = 0; i < DEFAULT_SHUTDOWN_NOW_ATTEMPT_TIME; i++) {
          if (!threadPool.awaitTermination(DEFAULT_SHUT_DOWN_AWAIT_TIME, TimeUnit.MILLISECONDS)) {
            threadPool.shutdownNow();
          }
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        1,
        1,
        SLEEP_TIME_MILLIS_SEC,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(1),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );

    threadPool.execute(new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(SLEEP_TIME_MILLIS_SEC);
        } catch (InterruptedException e) {
          try {
            Thread.sleep(SLEEP_TIME_MILLIS_SEC);
          } catch (InterruptedException interruptedException) {
            System.out.println("I got to terminate!");
          }
        }
      }
    });
    threadPoolShutDown(threadPool);
  }
}
