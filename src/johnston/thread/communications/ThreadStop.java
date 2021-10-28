package johnston.thread.communications;

/**
 * Calling stop() method of a thread can immediately terminate that thread.
 *
 * This operation is not safety. If the thread being stopped holds a lock, then it
 * would not release it. If the thread being stopped is using database, then it would
 * cause data inconsistency. So this method is @Deprecated.
 */
public class ThreadStop {
  private static long selfIncrement;

  static class ThreadDemo extends Thread {
    public ThreadDemo(String name) {
      super(name);
      System.out.println("Thread with name: " + this.getName() + " created.");
      selfIncrement = 0;
    }

    public void run() {
      System.out.println("Thread with name: \"" + this.getName() + "\" is running.");

      while(true) {
        selfIncrement++;
      }
    }
  }

  public static void main(String[] args) {
    Thread incrementThread = new ThreadDemo("IncrementThread");
    incrementThread.start();

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("(Danger) Stop " + incrementThread.getName());
    incrementThread.stop();
    System.out.println("Final result: " + selfIncrement);
  }
}
