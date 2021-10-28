package johnston.thread.communications;

/**
 * When interrupting a sleeping or joined thread, it will throw InterruptedException.
 */
public class ThreadInterruptedException {
  private static final int SLEEP_MILLI_SEC = 10000;

  static class SleepThread extends Thread {
    public SleepThread(String name) {
      super(name);
      System.out.println("Thread with name: " + this.getName() + " created.");
    }

    public void run() {
      try {
        Thread.sleep(SLEEP_MILLI_SEC);
      } catch (InterruptedException e) {
        System.out.println("I got interrupted when sleeping, and threw InterruptedException.");
        System.out.println("Error message: " + e.getMessage() + "\n");
      }
    }
  }

  static class JoinedThread extends Thread {
    public JoinedThread(String name) {
      super(name);
      System.out.println("Thread with name: " + this.getName() + " created.");
    }

    public void run() {
      try {
        Thread.currentThread().join();
      } catch (InterruptedException e) {
        System.out.println("I got interrupted when is joined, and threw InterruptedException.");
        System.out.println("Error message: " + e.getMessage() + "\n");
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Thread sleepThread = new SleepThread("SleepThread");
    sleepThread.start();
    Thread.sleep(1);

    System.out.println(Thread.currentThread().getName() + " is to interrupt a sleeping thread");
    sleepThread.interrupt();

    Thread joinedThread = new JoinedThread("JoinedThread");
    joinedThread.start();
    Thread.sleep(1);

    System.out.println(Thread.currentThread().getName() + " is to interrupt a joined thread");
    joinedThread.interrupt();
  }
}
