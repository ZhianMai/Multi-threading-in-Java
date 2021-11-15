package johnston.thread.thread_safety_and_locking;

/**
 *  Variable modified by keyword volatile has memory barrier which prevent compiler and CPU from
 *  out-of-order executing, and forcing write-through policy when the value is modified.
 *
 *  In this demo, if we remove the volatile keyword, then the spinlock will never unlock.
 */
public class VolatileDemo {
  // private static boolean flag = false;
  private volatile static boolean flag = false;

  static class SpinLock implements Runnable {

    @Override
    public void run() {
      int i = 0;
      while (!flag) {
        i++;
      }
      System.out.println("Spinlock unlocked.");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Thread spinLock = new Thread(new SpinLock());
    spinLock.start();
    Thread.sleep(1000);
    flag = true;
    System.out.println("Main thread unlocked the spinlock.");
  }
}
