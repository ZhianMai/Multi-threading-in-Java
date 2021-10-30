package johnston.thread.basic.creation.executors;

import java.util.concurrent.*;

/**
 * Using the Executor factory class to create a thread pool, then use the thread pool to run
 * threads.
 *
 * pool.execute() method accepts Runnable and Callable instance, and no return.
 * pool.execute(Runnable thread);
 * pool.submit(Callable<T> thread);
 *
 * pool.submit() method accepts Callable instance, and can return T.
 * Future<T> future = pool.submit(Callable<T> thread);
 */
public class ExecutorReturnablePool {
  private static final int DEFAULT_COMPUTE_TIMES = 10000;
  private static final int SLEEP_MILLI_SEC = 1000;
  private static final int DEFAULT_THREAD_POOL_SIZE = 3;

  private static ExecutorService pool = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

  static class RunnableThread implements Runnable {
    @Override
    public void run() {
      System.out.println(getCurThreadName() + " thread started.");
      try {
        Thread.sleep(SLEEP_MILLI_SEC);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      for (int i = 0; i < DEFAULT_COMPUTE_TIMES / 2; i++) {
        if (Math.random() < 1.0 / DEFAULT_COMPUTE_TIMES) {
          System.out.println(getCurThreadName() + " Jackpot!");
        }
      }
      System.out.println(getCurThreadName() + " thread finished.");
    }
  }

  // Callable interface implementation
  static class ReturnableThread implements Callable<Long> {
    // Call method has return type
    public Long call() throws Exception{
      long startTime = System.currentTimeMillis();
      System.out.println(getCurThreadName() + " thread started.");
      Thread.sleep(SLEEP_MILLI_SEC);

      for (int i = 0; i < DEFAULT_COMPUTE_TIMES; i++) {
        if (Math.random() < 1.0 / DEFAULT_COMPUTE_TIMES) {
          System.out.println(getCurThreadName() + " Jackpot!");
        }
      }

      long used = System.currentTimeMillis() - startTime;
      System.out.println(getCurThreadName() + " thread finished.");
      return used;
    }
  }

  private static String getCurThreadName() {
    return Thread.currentThread().getName();
  }

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    Runnable runnable = new RunnableThread();
    pool.execute(runnable);
    pool.submit(runnable);

    Future future = pool.submit(new ReturnableThread());
    Long result = (Long) future.get();
    System.out.println("Returnable thread runtime was: " + result);
    pool.shutdown(); // If not shutting down thread pool, program will not exit.
  }
}
