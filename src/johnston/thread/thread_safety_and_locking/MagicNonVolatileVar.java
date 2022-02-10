package johnston.thread.thread_safety_and_locking;

public class MagicNonVolatileVar {
  private static boolean flag = false;
  private static int SLEEP_MILLI_SEC = 100;
  private static volatile long counter = 0L;

  public static class IncrementThread extends Thread {
    @Override
    public void run() {
      int count = 1000000;

      while (count-- != 0) {
        counter++;
      }
    }
  }

  public static class DemoThread extends Thread {
    @Override
    public void run() {
      int i = 0;

      while (!flag) {
        System.out.print("Running...");
        i++;
      }

      System.out.println("\n\n\nThread finished running: " + i +"\n");
    }
  }

  public static void main(String[] args) throws InterruptedException {
//    Thread demoThread = new DemoThread();
//    demoThread.start();
//    Thread.sleep(SLEEP_MILLI_SEC);
//    flag = true;
//    System.out.println("\n\nMain thread finished...");
    Thread threadA = new IncrementThread();
    //Thread threadB = new IncrementThread();
    threadA.start();
    //threadB.start();
    threadA.join();
    //threadB.join();
    System.out.println(counter);
  }
}
