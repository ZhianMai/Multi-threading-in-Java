package johnston.thread.basic.properties;

/**
 * In general, a process would not exit execution as long as it has at least one running thread.
 * But some threads are doing background task, like file auto saving thread, or producer thread
 * using external resources. These threads should not block the process from exiting.
 *
 * We can set these threads as daemon thread, so they won't bother the main thread when main is
 * about to exit.
 */
public class DaemonThread {
  public static void main(String[] args) {
    Thread daemonThread = new Thread(() -> {
      System.out.println("Running: " + Thread.currentThread().getName());
      System.out.println("Is it daemon? " + Thread.currentThread().isDaemon());

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("I'm still running: " + Thread.currentThread().getName());
    });
    daemonThread.setName("Daemon X");
    // daemonThread.setDaemon(true);
    //daemonThread.setDaemon(false);
    daemonThread.start();

    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
