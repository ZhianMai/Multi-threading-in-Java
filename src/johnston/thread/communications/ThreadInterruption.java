package johnston.thread.communications;

import java.util.Random;

/**
 * When threadA calls threadB.interrupt(), threadB can receive an interruption signal, and
 * decide what to do next. So calling interrupt() to threadB does not stop or pause threadB if
 * it's running (not waiting or sleeping).
 *
 * This demo is to interrupt a thread.
 */
public class ThreadInterruption {
  private static final int SLEEP_MILLI_SEC = 100;

  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
      System.out.println("Thread with name: " + this.getName() + " created.");
    }

    public void run() {
      while(true) {
        if (Thread.currentThread().isInterrupted()) {
          System.out.println(Thread.currentThread().getName() + " got an interruption.");
          break;
        }
      }
      System.out.println(Thread.currentThread().getName() + " is about to terminate.");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Thread threadToInterrupt = new ThreadDemo("ThreadToInterrupt");
    threadToInterrupt.start();
    Thread.sleep(SLEEP_MILLI_SEC);
    threadToInterrupt.interrupt();
  }
}
