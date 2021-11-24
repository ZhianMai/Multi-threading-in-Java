package johnston.thread.demo.multi_threading.three_sum_guava;

import com.google.common.util.concurrent.*;

import javax.annotation.Nullable;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreeSumGuava {
  /**
   * Partition the array into 10 parts. The total amount of two sum subtask is
   * 10 + 45 = 55. The first 10 subtasks are finding two sum within the same part,
   * and the second 45 subtasks are finding two sum from any two different parts.
   * Combination(2, 10) = 45.
   */
  private static final int DEFAULT_ARRAY_PARTITION_AMOUNT = 10;

  static class AccumulativeResult {
    AtomicInteger totalTask = new AtomicInteger(0);
    AtomicInteger finishedTask = new AtomicInteger(0);
    AtomicInteger twoSumResults = new AtomicInteger(0);

    public boolean isResultReady() {
      return totalTask.get() == finishedTask.get();
    }

    public int getResult() {
      if (totalTask.get() == finishedTask.get()) {
        System.out.println("The result is: " + (twoSumResults.get() / 3));
      }

      return twoSumResults.get();
    }
  }

  public int threeSum(int[] array, int target) {
    return this.threeSum(array, target, DEFAULT_ARRAY_PARTITION_AMOUNT);
  }

  public int threeSum(int[] array, int target, int partitionAmount) {
    partitionAmount = Math.min(array.length, partitionAmount);
    int[] twoSumTarget = ThreeSumUtil.getTwoSumTarget(array, target);
    // Ceiling operation
    int sectionLength = (array.length - 1) / partitionAmount + 1;
    int[] sectionsBeginIdx = new int[partitionAmount + 1];
    for (int i = 1; i < partitionAmount; i++) {
      sectionsBeginIdx[i] = sectionsBeginIdx[i - 1] + sectionLength;
    }
    sectionsBeginIdx[partitionAmount] = array.length;

    AccumulativeResult accuResult = new AccumulativeResult();
    int threadAmount = Math.max(Runtime.getRuntime().availableProcessors() - 1, 1);
    ThreadPoolExecutor tPool = new ThreadPoolExecutor(
        threadAmount,
        threadAmount,
        1,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(partitionAmount),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
    ListeningExecutorService gPool = MoreExecutors.listeningDecorator(tPool);

    // Find two sum
    accuResult.totalTask.addAndGet(partitionAmount);// New job++

    for (int i = 0; i < partitionAmount; i++) {
      // Create callable two sum subtask
      TwoSumTask subTask = new TwoSumTask(array, twoSumTarget,
          sectionsBeginIdx[i], sectionsBeginIdx[i + 1]);

      // Create callback hook
      FutureCallback<Integer> subTaskHook = new FutureCallback<Integer>() {
        @Override
        public void onSuccess(@Nullable Integer result) {
          accuResult.twoSumResults.addAndGet(result);
          accuResult.finishedTask.incrementAndGet();
        }

        @Override
        public void onFailure(Throwable throwable) {
          try {
            throw throwable;
          } catch (Throwable e) {
            // e.printStackTrace();
          }
        }
      };
      ListenableFuture<Integer> twoSumFuture = gPool.submit(subTask);
      Futures.addCallback(twoSumFuture, subTaskHook);
    }

    while (!accuResult.isResultReady()) {
      Thread.yield(); // Self spin here, or do other tasks
    }
    return (accuResult.getResult() / 3);
  }
}
