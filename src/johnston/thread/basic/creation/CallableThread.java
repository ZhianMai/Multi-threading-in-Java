package johnston.thread.basic.creation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableThread {
  public static final int SLEEP_MILLI_SEC = 1000;
  public static final int COMPUTE_TIMES = 100000000;

  // Callable interface implementation
  static class ReturnableTask implements Callable<Long> {
    // Call method has return type
    public Long call() throws Exception{
      long startTime = System.currentTimeMillis();
      System.out.println(getCurThreadName() + " thread started.");
      Thread.sleep(SLEEP_MILLI_SEC);

      for (int i = 0; i < COMPUTE_TIMES; i++) {
        if (Math.random() < 1.0 / COMPUTE_TIMES) {
          System.out.println(getCurThreadName() + " Jackpot!");
        }
      }

      long used = System.currentTimeMillis() - startTime;
      System.out.println(getCurThreadName() + " thread finished.");
      return used;
    }
  }

  private static String getCurThreadName() {
    return Thread.currentThread().getName();
  }

  public static void main(String args[]) throws InterruptedException {
    ReturnableTask task = new ReturnableTask();
    FutureTask<Long> futureTask = new FutureTask<>(task);
    Thread thread = new Thread(futureTask, "Returnable_Thread");//⑤
    thread.start();
    Thread.sleep(SLEEP_MILLI_SEC / 2);
    System.out.println(getCurThreadName() + " is running on its work");

    for (int i = 0; i < COMPUTE_TIMES / 2; i++) {
      if (Math.random() < 1.0 / COMPUTE_TIMES) {
        System.out.println(getCurThreadName() + " Jackpot!");
      }
    }

    System.out.println(getCurThreadName() + " get returnable thread result.");
    try {
      double result = futureTask.get();
      System.out.println(thread.getName()+" thread running time：" + result);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    System.out.println(getCurThreadName() + " finished.");
  }
}

