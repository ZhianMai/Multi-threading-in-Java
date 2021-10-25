package johnston.thread.basic.properties;

/**
 * run() method contains the logic/task for a thread to execute.
 * start() method starts a thread.
 *
 * So calling threadA.run() is not starting a thread but running the task of threadA in the current
 * thread memory stack.
 */
public class RunStartDiff {
  static class ThreadDemo extends Thread {
    public void run() {
      System.out.println("run() method called by " + Thread.currentThread().getName());
    }
  }

  public static void main(String[] args) {
    ThreadDemo threadDemo = new ThreadDemo();
    threadDemo.run();
    threadDemo.start();
  }
}
