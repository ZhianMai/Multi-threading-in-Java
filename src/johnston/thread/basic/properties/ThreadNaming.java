package johnston.thread.basic.properties;

/**
 * The thread can have a config name, by passing the name to the constructor when creating the
 * thread, or by calling setName().
 *
 * To get the name of current running thread, call Thread.currentThread().getName().
 */
public class ThreadNaming {
  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
      System.out.println("Thread with name \"" + this.getName() + "\" created.");
      System.out.println("Created by : \"" + Thread.currentThread().getName() + "\" thread.\n");
    }

    public void run() {
      System.out.println("Thread with name: \"" + this.getName() + "\" created.");
      Thread.currentThread().setName("Johnston");
      System.out.println("Thread changed to new name: " + this.getName() + ".");
    }
  }

  public static void main(String[] args) {
    System.out.println("Main thread with name : \"" + Thread.currentThread().getName() +
        "\" is running.");
    Thread.currentThread().setName("Johnston's main thread");
    System.out.println("New name for main thread: " + Thread.currentThread().getName());

    System.out.println();
    Thread newThread = new ThreadDemo("thread_a");
    newThread.start();
  }
}
