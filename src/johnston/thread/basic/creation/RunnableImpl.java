package johnston.thread.basic.creation;

/**
 * Thread creation by implementing Runnable interface
 *
 * Notice the Runnable interface has run() method only, so there is no corresponding
 * fields in Thread class.
 */
public class RunnableImpl {
  static class RunnableThread implements Runnable {
    @Override
    public void run() {
      System.out.println("Running with name: \"" + Thread.currentThread().getName() + "\"");
    }
  }

  public static void main(String[] args) {
    Runnable runnable = new RunnableThread();
    String threadName = "New runnable thread";
    Thread newThread = new Thread(runnable, threadName);
    newThread.start();
  }
}
