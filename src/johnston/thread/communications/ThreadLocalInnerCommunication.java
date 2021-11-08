package johnston.thread.communications;

import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadLocal is a convenient way to ensure data safety in multi-threading. It's like a hash map
 * where the key is the thread task id, and the value is the variable belonging to that thread task
 * only. It's a more efficient way to ensure data-racing free than using "synchronized" keyword.
 * ThreadLocal also helps decouple among multi-threading methods and class when variable sharing is
 * required.
 *
 * ThreadLocal is usually static and final. Static ensures it can be used across all threads, and
 * final ensures it won't be replaced causing threads losing their data.
 *
 * Always use remove() method to remove instance when the current thread is about to terminate.
 *
 * In this demo, each Runnable task has its own unique random number n, and it creates a variable
 * in the ThreadLocal object, then increment that variable n times. The result shows that
 * ThreadLocal would not mix the variables that each of them belongs to one Runnable task only.
 */
public class ThreadLocalInnerCommunication {
  private static final int DEFAULT_THREAD_AMOUNT;
  private static final int DEFAULT_CPU_CORE_AMOUNT;
  private static final int DEFAULT_THREAD_RUN_TIMES = 10;

  static {
    DEFAULT_CPU_CORE_AMOUNT = Runtime.getRuntime().availableProcessors();
    DEFAULT_THREAD_AMOUNT = DEFAULT_CPU_CORE_AMOUNT;
  }

  static class ThreadLocalIncrement extends Thread {
    private Random random = new Random();

    public ThreadLocalIncrement(String name) {
      super(name);
    }

    public void run() {
      int executeTimes = random.nextInt(1000);

      if (THREAD_LOCAL_DATA.get() == null) {
        THREAD_LOCAL_DATA.set(0);
      }

      for (int i = 0; i < executeTimes; i++) {
        THREAD_LOCAL_DATA.set(THREAD_LOCAL_DATA.get() + 1);
      }


      System.out.println("The amount of execution times is " + executeTimes + ", and I have " +
          "executed " + THREAD_LOCAL_DATA.get() + " times.");
    }
  }

  private static final ThreadLocal<Integer> THREAD_LOCAL_DATA = new ThreadLocal<>();

  public static void main(String[] args) {
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        DEFAULT_CPU_CORE_AMOUNT,
        DEFAULT_CPU_CORE_AMOUNT,
        10,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(DEFAULT_THREAD_AMOUNT),
        new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
    ) {
      @Override
      protected void afterExecute(Runnable target, Throwable t) {
        // Ensure no memory leak.
        THREAD_LOCAL_DATA.remove();
      }
    };

    Thread threadLocalIncrement = new ThreadLocalIncrement("ThreadLocal Increment");

    for (int i = 0; i < DEFAULT_CPU_CORE_AMOUNT; i++) {
      threadPool.execute(threadLocalIncrement);
    }

    threadPool.shutdown();
  }
}
