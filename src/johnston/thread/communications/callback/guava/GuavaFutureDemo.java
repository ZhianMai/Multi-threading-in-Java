package johnston.thread.communications.callback.guava;

import com.google.common.util.concurrent.*;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuavaFutureDemo {
  public static final int DEFAULT_SLEEP_MILLI_SEC = 3000;

  static class BoilWaterTask implements Callable<Boolean> {
    @Override
    public Boolean call() throws Exception {
      try {
        System.out.println("Boil water task: 1. Clean the pot");
        System.out.println("Boil water task: 2. Boil water");
        Thread.sleep(DEFAULT_SLEEP_MILLI_SEC);
        System.out.println("Boil water task: 3. Water boiled, all tasks finished.");
      } catch (InterruptedException e) {
        System.out.println("Exception occurred, boil water job aboard.");
        return false;
      }
      return true;
    }
  }

  static class CleanUpTask implements Callable<Boolean> {
    @Override
    public Boolean call() throws Exception {
      try {
        System.out.println("Clean up task: 1. Clean the cup");
        Thread.sleep(DEFAULT_SLEEP_MILLI_SEC);
        System.out.println("Clean up task: 2. Cup cleaned, all tasks finished.");
      } catch (InterruptedException e) {
        System.out.println("Exception occurred, clean up job aboard.");
        return false;
      }
      return true;
    }
  }

  static class TeaService {
    boolean waterBoiled = false;
    boolean cupCleaned = false;

    public void prepareTea() {
      if (waterBoiled && cupCleaned) {
        System.out.println("Tea is ready.");
        this.waterBoiled = false;
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Thread.currentThread().setName("Tea service");

    TeaService teaService = new TeaService();
    Callable<Boolean> boilWaterTask = new BoilWaterTask();
    Callable<Boolean> cleanUpTask = new CleanUpTask();

    ExecutorService jobPool = Executors.newFixedThreadPool(10);
    ListeningExecutorService gPool = MoreExecutors.listeningDecorator(jobPool);

    FutureCallback<Boolean> boilWaterHook = new FutureCallback<Boolean>() {
      @Override
      public void onSuccess(@Nullable Boolean aBoolean) {
        if (aBoolean) {
          teaService.waterBoiled = true;
          teaService.prepareTea();
        }
      }

      @Override
      public void onFailure(Throwable throwable) {
        System.out.println("Boil water task failed");
      }
    };

    ListenableFuture<Boolean> boilWaterFuture = gPool.submit(boilWaterTask);
    Futures.addCallback(boilWaterFuture, boilWaterHook);

    ListenableFuture<Boolean> cleanUpCupFuture = gPool.submit(cleanUpTask);
    Futures.addCallback(cleanUpCupFuture, new FutureCallback<Boolean>() {
      @Override
      public void onSuccess(@Nullable Boolean aBoolean) {
        if (aBoolean) {
          teaService.cupCleaned = true;
          teaService.prepareTea();
        }
      }

      @Override
      public void onFailure(Throwable throwable) {
        System.out.println("Clean up cup task failed");
      }
    });

    System.out.println("Prepare some snack");
    Thread.sleep(1);
    System.out.println("Snack ready");

    jobPool.shutdown();
  }
}
