package johnston.thread.communications;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Calling wait() method of a lock can let it the thread who holds this lock release it, so another
 * thread can enter the critical section.
 *
 * In this Demo, thread A will enter the critical section first, but needs to wait 1 sec. Thread A
 * will release the lock and let thread B enter. Thread A will continue right after the wait()
 * method if other threads notify it or the wait() is countdown.
 *
 * When calling LOCK.wait(), the synchronized block holds LOCK so it can release. If using other
 * objects to call wait(), then it will throw IllegalMonitorStateException.
 */
public class ThreadWait {
  private static AtomicInteger runtimeCounter = new AtomicInteger(0);
  private static final Object LOCK = new Object();

  static class DemoThread extends Thread {
    public DemoThread(String name) {
      super(name);
    }

    @Override
    public void run() {
      try {
        pickSomeoneToWait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private static void pickSomeoneToWait() throws InterruptedException {
    synchronized (LOCK) {
      System.out.println(Thread.currentThread().getName() + " owns the lock.");
      runtimeCounter.incrementAndGet();

      if (runtimeCounter.get() == 1) {
        System.out.println("Value of runtimeCounter is " + runtimeCounter + ". " +
            Thread.currentThread().getName() + " needs to wait.");
        LOCK.wait(1000);
      }
      System.out.println(Thread.currentThread().getName() + " will release the lock.");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Thread threadA = new DemoThread("Thread A");

    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        4,
        4,
        10,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(4),
        new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
    );

    threadPool.execute(threadA);
    threadPool.execute(threadA);
    threadPool.shutdown();
  }
}
