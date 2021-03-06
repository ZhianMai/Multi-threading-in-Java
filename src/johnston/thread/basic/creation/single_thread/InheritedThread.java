package johnston.thread.basic.creation.single_thread;

/**
 * Thread creation by extending Thread class
 */
public class InheritedThread {
  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
      System.out.println("Thread with name: " + this.getName() + " created.");
      System.out.println("Created by : " + Thread.currentThread().getName() + " thread.\n");
    }

    public void run() {
      System.out.println("Thread with name: \"" + Thread.currentThread().getName() + "\" is running.");
    }
  }

  public static void main(String[] args) {
    Thread newThread = new ThreadDemo("thread_a");
    newThread.start();

    InheritedThread inheritedThread = new InheritedThread();
    Thread newThreadB = new InheritedThread.ThreadDemo("threa_b");
    newThreadB.start();
    newThreadB.start();
  }
}
