package johnston.thread.thread_safety_and_locking;

import java.util.concurrent.*;

/**
 * Util class CyclicBarrier is like reusable CountdownLatch. It's a barrier that can keep all
 * caller threads to wait at the barrier point until all ready, then notifies all threads.
 */
public class CyclicBarrierDemo {
  private static final int DEFAULT_TASK_AMOUNT = 5;
  private static CyclicBarrier barrier;

  private static String getRunningThreadName() {
    return Thread.currentThread().getName();
  }

  static Runnable myThread = () -> {
    try{
      Thread.sleep(1000);
      System.out.println(getRunningThreadName() + " reaches barrier A");
      barrier.await();
      System.out.println(getRunningThreadName() + " cross barrier A");

      Thread.sleep(2000);
      System.out.println(getRunningThreadName() + " reaches barrier B");
      barrier.await();
      System.out.println(getRunningThreadName() + " cross barrier B");
    } catch (InterruptedException | BrokenBarrierException e) {
      e.printStackTrace();
    }
  };

  public static void main(String[] args) {
    barrier = new CyclicBarrier(DEFAULT_TASK_AMOUNT, () -> {
      System.out.println("CyclicBarrier: all threads arrived");
    });

    ThreadPoolExecutor executor = new ThreadPoolExecutor(
        DEFAULT_TASK_AMOUNT,
        DEFAULT_TASK_AMOUNT,
        1,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(DEFAULT_TASK_AMOUNT),
        new ThreadPoolExecutor.DiscardPolicy() // Silent AbortPolicy()
    );

    for (int i = 0; i < DEFAULT_TASK_AMOUNT; i++) {
      executor.execute(myThread);
    }
    executor.shutdown();
  }
}
