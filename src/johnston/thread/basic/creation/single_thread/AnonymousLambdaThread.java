package johnston.thread.basic.creation.single_thread;

/**
 * Thread creation by creating anonymous Threads using lambda expression.
 *
 * No guarantee on the thread running order! The result may not be 0-1-2-3.
 */
public class AnonymousLambdaThread {
  private static final int THREAD_COUNT = 4;

  public static void main(String[] args) {
    Thread[] fakeThreadPool = new Thread[THREAD_COUNT];

    for (int i = 0; i < fakeThreadPool.length; i++) {
      // Lambda expression
      fakeThreadPool[i] = new Thread(() -> {
        System.out.println("Running: " + Thread.currentThread().getName());
      });
      fakeThreadPool[i].setName(String.valueOf(i));
    }

    for (Thread thread : fakeThreadPool) {
      thread.start();
    }
  }
}
