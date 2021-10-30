package johnston.thread.basic.creation.single_thread;

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

  public static void main(String[] args) throws InterruptedException {
    Runnable runnable = new RunnableThread();
    Thread newThreadA = new Thread(runnable, "New runnable thread A");
    newThreadA.start();

    Thread.sleep(10);

    Thread newThreadB = new Thread(runnable, "New runnable thread B");
    newThreadB.start();
  }
}
