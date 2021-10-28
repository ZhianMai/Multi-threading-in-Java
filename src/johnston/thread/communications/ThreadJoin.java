package johnston.thread.communications;

import java.util.Random;

/**
 * When calling threadB.join() in threadA, threadA would wait until threadB finishes the
 * work.
 *
 * Suppose threadA needs the result from threadB, then threadA can call threadB.join() to
 * wait for the threadB instead of using a while loop to run a spinlock.
 *
 * ThreadA that called join() may throw InterruptedException if other threads interrupt ThreadA.
 *
 * This demo creates two threads (A & B) to run a random int generator until get the target number.
 * Main thread will wait until A exits, but will not wait B. So main thread always get the target
 * number from A and needs some luck to get the same from B.
 */
public class ThreadJoin {
  private static final int TARGET = 10000000;
  private static boolean gotTarget;

  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
      System.out.println("Thread with name: " + this.getName() + " created.");
      gotTarget = false;
    }

    public void run() {
      System.out.println("Thread with name: \"" + this.getName() + "\" is running.");
      Random random = new Random();
      int temp = 0;

      while (temp != TARGET / 2) {
        temp = random.nextInt(TARGET);
      }
      gotTarget = true;
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Thread workingThreadA = new ThreadDemo("WorkingThreadA");
    workingThreadA.start();
    workingThreadA.join();
    Thread.sleep(1);
    System.out.println("Called join(), got the target result? " + gotTarget);
    System.out.println();

    Thread workingThreadB = new ThreadDemo("WorkingThreadA");
    workingThreadB.start();
    Thread.sleep(1);
    System.out.println("Not called join(), got the target result? " + gotTarget);
  }
}
