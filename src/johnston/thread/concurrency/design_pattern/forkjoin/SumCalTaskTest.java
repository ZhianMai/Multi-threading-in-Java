package johnston.thread.concurrency.design_pattern.forkjoin;

import java.util.concurrent.*;

public class SumCalTaskTest {
  public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
    Long startMin = System.currentTimeMillis();
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    SumCalculationTask calTask =
        new SumCalculationTask(0, Integer.MAX_VALUE / 2);
    Future<Long> future =forkJoinPool.submit(calTask);
    Long result = future.get(1, TimeUnit.HOURS);
    System.out.println("The result is: " + result);
    System.out.println("Time taken: " + ((System.currentTimeMillis() - startMin) / 1000) + " s");
    calSumTimeBenchMark(0, Integer.MAX_VALUE / 2);
  }

  private static void calSumTimeBenchMark(int begin, int end) {
    long result = 0;
    Long startMin = System.currentTimeMillis();

    for (int i = begin; i <= end; i++) {
      result += i;
    }
    System.out.println("Result: " + result);
    System.out.println("Benchmark time taken: " +
        ((System.currentTimeMillis() - startMin) / 1000) + " s");
  }
}
