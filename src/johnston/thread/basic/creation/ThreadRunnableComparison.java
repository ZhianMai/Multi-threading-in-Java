package johnston.thread.basic.creation;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * In OOD, favor interface over inheritance, so implementing Runnable interface for thread creation
 * is preferable.
 *
 * Also, using Runnable interface can greatly decouple data and logic.
 */
public class ThreadRunnableComparison {
  public static final int APPLE_AMOUNT = 5;

  static class AliceGroup extends Thread {
    private int appleLeft = APPLE_AMOUNT;

    public void run() {
      Thread.currentThread().setName("Alice group " + Thread.currentThread().getId());

      for (int i = 0; i < APPLE_AMOUNT; i++) {
        if (appleLeft > 0) {
          System.out.println(Thread.currentThread().getName() + " ate an apple.");
          appleLeft--;
        }
      }
    }
  }

  static class BobGroup implements Runnable {
    // Avoid data racing
    private int appleLeft = APPLE_AMOUNT;

    private synchronized boolean eatApple() {
      if (appleLeft > 0) {
        appleLeft--;
        return true;
      }
      return false;
    }

    @Override
    public void run() {
      Thread.currentThread().setName("Bob group " + Thread.currentThread().getId());

      for (int i = 0; i < APPLE_AMOUNT; i++) {
        if (eatApple()) {
          System.out.println(Thread.currentThread().getName() + " ate an apple.");
        }
      }
    }
  }

  public synchronized static void main(String[] args) {
    System.out.println("Alice group: ");
    Thread groupA = new AliceGroup();
    Thread groupB = new AliceGroup();
    groupA.start();
    groupB.start();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("Bob group: ");
    Runnable bobA = new BobGroup();
    groupA = new Thread(bobA);
    groupB = new Thread(bobA);
    groupA.start();
    groupB.start();
  }
}
