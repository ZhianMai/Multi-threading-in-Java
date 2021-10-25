package johnston.thread.basic.properties;

/**
 * Each thread has a unique id assigned by JVM.
 */
public class ThreadID {
  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
    }

    public void run() {
      System.out.println("Thread with name : \"" + this.getName() + "\" is running.");
      System.out.println("Thread id is: " + this.getId());
    }
  }

  public static void main(String[] args) {
    System.out.println("Main thread id is: " + Thread.currentThread().getId() + "\n");

    Thread newThread = new ThreadDemo("thread_a");
    newThread.start();
  }
}
