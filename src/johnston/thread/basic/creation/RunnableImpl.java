package johnston.thread.basic.creation;

public class RunnableImpl {
  public static final int THREAD_COUNT = 4;

  static class RunnableThread implements Runnable {
    @Override
    public void run() {
      System.out.println("Running: " + Thread.currentThread().getName());
    }
  }

  public static void main(String[] args) {
    Runnable runnable = new RunnableThread();
    Thread newThread = new Thread(runnable);
    newThread.start();
  }
}
