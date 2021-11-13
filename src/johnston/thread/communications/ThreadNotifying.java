package johnston.thread.communications;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Calling notify() from a lock object can pick a thread from the waiting pool to start. The
 * waiting pool holds threads waiting for that lock. The thread that calls notify() is in RUNNING
 * state, and the awake thread is in BLOCKED state instead of WAITING. It will be the next one
 * to enter critical section.
 * <p>
 * In this demo, many producer threads and many consumers thread runs at the same time. If a
 * consumer cannot get resource, it will wait. If a producer notice consumers are waiting, it
 * will notify them to wake up.
 */
public class ThreadNotifying {
  private static final int DEFAULT_RUNTIMES = 50;
  private static AtomicInteger result = new AtomicInteger(0);
  private static AtomicBoolean consumerWaiting = new AtomicBoolean(false);
  private static final Object CONSUMER_LOCK = new Object();
  private static final Object PRODUCER_LOCK = new Object();

  static class ConsumerThread extends Thread {
    private AtomicInteger runtimes;

    public ConsumerThread(String name, int runtimes) {
      super(name);
      this.runtimes = new AtomicInteger(runtimes);
    }

    @Override
    public void run() {
      try {
        consume(runtimes);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  static class ProducerThread extends Thread {
    private AtomicInteger runtimes;

    public ProducerThread(String name, int runtimes) {
      super(name);
      this.runtimes = new AtomicInteger(runtimes);
    }

    @Override
    public void run() {
      try {
        produce(runtimes);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private static void consume(AtomicInteger runtimes) throws InterruptedException {
    while (runtimes.decrementAndGet() > 0) {
      while (result.get() == 0) {
        System.out.println(Thread.currentThread().getName() + ": no result so far. Wait.");
        consumerWaiting.set(true);

        try {
          synchronized (CONSUMER_LOCK) {
            CONSUMER_LOCK.wait();
          }
        } catch (Exception e) {
          System.out.println("error: " + e.getMessage());
        }
      }

      System.out.println("I got the result: " + (result.decrementAndGet() + 1));
      consumerWaiting.set(false);
      Thread.sleep(10);
    }
  }

  private static void produce(AtomicInteger runtimes) throws InterruptedException {
    while (runtimes.decrementAndGet() > 0) {
      Thread.sleep(20);
      System.out.println("Produce 1.");
      result.incrementAndGet();

      if (consumerWaiting.get()) {
        synchronized (CONSUMER_LOCK) {
          CONSUMER_LOCK.notify();
          System.out.println("Wake up consumer.");
        }
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Thread consumerA = new ConsumerThread("Consumer A", DEFAULT_RUNTIMES);
    Thread producerA = new ProducerThread("Producer A", DEFAULT_RUNTIMES);

    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        4,
        4,
        10,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(4),
        new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
    );

    threadPool.execute(consumerA);
    threadPool.execute(producerA);

    threadPool.shutdown();
    threadPool.awaitTermination(10000, TimeUnit.MILLISECONDS);
    threadPool.shutdownNow();
  }
}
