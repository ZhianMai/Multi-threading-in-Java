package johnston.thread.basic.properties.single_thread;

/**
 * A thread has six types of states: new, runnable, blocked, waiting, timed_waiting, and
 * terminated. They represent the life cycle of threads.
 *
 * Blocked state happens when a thread is waiting to enter a synchronized block.
 */
public class ThreadState {
  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
      System.out.println("Thread with name: \"" + this.getName() + "\" created.");
      System.out.println("Inside constructor, the state is: " + this.getState() + "\n");
    }

    public synchronized void run() {
      System.out.println("Thread with name: \"" + this.getName() + "\" is running.");
      System.out.println("Inside run(), the state is: " + this.getState() + "\n");

      try {
        this.wait(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // Enter the critical section.
      criticalSection();
    }
  }

  public static void main(String[] args) {
    // Create a thread, state is NEW.
    Thread newThread = new ThreadDemo("Johnston's thread");
    System.out.println("After creation, the state is: " + newThread.getState() + "\n");

    // Run the thread, then the state is RUNNABLE.
    newThread.start();

    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // The thread is waiting, and the state is WAITING.
    System.out.println("Thread is waiting, and the state is: " + newThread.getState() + "\n");

    // Create another thread to enter the critical section, forcing other thread being blocked
    // if trying to enter the critical section.
    Thread temp = new Thread(() -> {
      criticalSection();
    });
    temp.start();

    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // Waiting for the critical section available, and the state is BLOCKED.
    System.out.println("Thread is waiting to enter the critical section, and the state is: " +
        newThread.getState() + "\n");

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // Finished running, the state is TERMINATED.
    System.out.println("Finished running, the state is: " + newThread.getState() + "\n");
  }

  private static synchronized void criticalSection() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
