package johnston.thread.communications;

import java.util.concurrent.locks.LockSupport;

/**
 * LockSupport is a util class provided by JUC. It allows a thread to sleep and let other threads
 * to wake it up explicitly. It's like using Thread.sleep() but allows other thread to wake it up,
 * and wound not throw interruption when interrupted. So it's more flexible than sleep().
 */
public class LockSupportDemo {
  public static void main(String[] args) throws InterruptedException {
    class DemoThread extends Thread {
      @Override
      public void run() {
        LockSupport.park(); // Thread.sleep()

        if (this.isInterrupted()) {
          System.out.println("Woke up by interruption");
        } else {
          System.out.println("Woke up by method unpark().");
        }
      }
    }

    DemoThread threadA = new DemoThread();
    DemoThread threadB = new DemoThread();
    threadA.start();
    threadB.start();
    Thread.sleep(10);
    threadA.interrupt();
    LockSupport.unpark(threadB); // Explicitly wake up threadB
  }
}
