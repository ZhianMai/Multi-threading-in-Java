package johnston.thread.basic.creation.single_thread;

/**
 * Thread creation by creating anonymous Threads.
 *
 * No guarantee on the thread running order! The result may not be 0-1-2-3.
 */
public class AnonymousThread {
  private static final int THREAD_COUNT = 4;

  public static void main(String[] args) {
    Thread[] fakeThreadPool = new Thread[THREAD_COUNT];

    for (int i = 0; i < fakeThreadPool.length; i++) {
      fakeThreadPool[i] = new Thread(new Runnable() {
        @Override
        public void run() {
          System.out.println("Running: " + Thread.currentThread().getName());
        }
      });
      fakeThreadPool[i].setName(String.valueOf(i));
    }

    for (Thread thread : fakeThreadPool) {
      thread.start();
    }
  }
}
