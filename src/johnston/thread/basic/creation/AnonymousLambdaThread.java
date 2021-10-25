package johnston.thread.basic.creation;

/**
 * Thread creation by creating anonymous Threads using lambda expression.
 *
 * No guarantee on the thread running order! The result may not be 0-1-2-3.
 */
public class AnonymousLambdaThread {
  private static final int THREAD_COUNT = 4;

  public static void main(String[] args) {
    Thread[] threadPool = new Thread[THREAD_COUNT];

    for (int i = 0; i < threadPool.length; i++) {
      threadPool[i] = new Thread(() -> {
        System.out.println("Running: " + Thread.currentThread().getName());
      });
      threadPool[i].setName(String.valueOf(i));
    }

    for (Thread thread : threadPool) {
      thread.start();
    }
  }
}
