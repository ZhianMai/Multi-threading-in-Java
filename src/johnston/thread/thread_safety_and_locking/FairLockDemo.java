package johnston.thread.thread_safety_and_locking;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Demo of using fair lock. Fair lock has high performance penalty so many threads are timeout
 * in waiting.
 *
 * Run this demo multiple times, and the fair lock can produce waiting timeout threads. If the
 * CPU has great performance, increase the THREAD_AMOUNT variable.
 */
public class FairLockDemo {
  private static final int THREAD_AMOUNT = 300;
  private static Lock lock;
  private static Random random;
  private static String[] readyThreadID;
  private static String[] gotLockThreadID;
  private static AtomicInteger readyTreadIDIdx;
  private static AtomicInteger gotLockTreadIDIdx;

  public FairLockDemo() {
    random = new Random();
    readyThreadID = new String[THREAD_AMOUNT];
    gotLockThreadID = new String[THREAD_AMOUNT];
    readyTreadIDIdx = new AtomicInteger(0);
    gotLockTreadIDIdx = new AtomicInteger(0);
  }

  static class MyThread extends Thread {
    private CountDownLatch countDownLatch;

    public MyThread(String threadName, CountDownLatch countDownLatch) {
      super(threadName);
      this.countDownLatch = countDownLatch;
      countDownLatch.countDown();
    }

    public void run() {
      try {
        countDownLatch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      readyThreadID[readyTreadIDIdx.getAndIncrement()] = this.getName();
      boolean gotLock = false;
      try {
        gotLock = lock.tryLock(10, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (gotLock) {
        try {
        gotLockThreadID[gotLockTreadIDIdx.getAndIncrement()] = this.getName();
        } finally {
          lock.unlock();
        }
      } else {
        System.out.println("Starvation!");
      }
    }
  }

  private Thread getThread(String threadName, CountDownLatch countDownLatch) {
    return new MyThread(threadName, countDownLatch);
  }

  private void fairLockDemo() throws InterruptedException {
    lock = new ReentrantLock(true);
    Thread[] threadList = new Thread[THREAD_AMOUNT];
    CountDownLatch countDownLatch = new CountDownLatch(THREAD_AMOUNT);
    readyTreadIDIdx.set(0);
    gotLockTreadIDIdx.set(0);

    for (int i = 0; i < THREAD_AMOUNT; i++) {
      String threadName = "Fair-" + i;
      threadList[i] = getThread(threadName, countDownLatch);
    }

    for (Thread thread : threadList) {
      thread.start();
    }

    for (Thread thread : threadList) {
      thread.join();
    }
    checkOrder();
  }

  private void unfairLockDemo() throws InterruptedException {
    lock = new ReentrantLock(false);
    Thread[] threadList = new Thread[THREAD_AMOUNT];
    CountDownLatch countDownLatch = new CountDownLatch(THREAD_AMOUNT);
    readyTreadIDIdx.set(0);
    gotLockTreadIDIdx.set(0);

    for (int i = 0; i < THREAD_AMOUNT; i++) {
      String threadName = "Unfair-" + i;
      threadList[i] = getThread(threadName, countDownLatch);
    }

    for (Thread thread : threadList) {
      thread.start();
    }

    for (Thread thread : threadList) {
      thread.join();
    }
    checkOrder();
  }

  private void checkOrder() {
    for (int i = 0; i < THREAD_AMOUNT; i++) {
      if (!readyThreadID[i].equals(gotLockThreadID[i])) {
        System.out.println("Order not good");
        return;
      }
    }
    System.out.println("In order");
  }

  public static void main(String[] args) throws InterruptedException {
    FairLockDemo demo = new FairLockDemo();
    System.out.println("Fair lock demo:");
    demo.fairLockDemo();
    System.out.println("Unfair lock demo:");
    demo.unfairLockDemo();
  }
}
