package johnston.thread.concurrency.design_pattern.forkjoin;

import java.util.concurrent.RecursiveTask;

public class SumCalculationTask extends RecursiveTask<Long> {
  private static final int MIN_LENGTH = 2;
  private int start;
  private int end;

  public SumCalculationTask(int start, int end) {
    this.start = start;
    this.end = end;
  }

  @Override
  protected Long compute() {
    long sum = 0;
    boolean isBaseCase = (end - start) <= MIN_LENGTH;

    if (isBaseCase) {
      for (int i = start; i <= end; i++) {
        sum += i;
      }
    } else {
      int mid = start + (end - start) / 2;
      SumCalculationTask fstTask = new SumCalculationTask(start, mid);
      SumCalculationTask secTask = new SumCalculationTask(mid + 1, end);
      fstTask.fork();
      secTask.fork();
      long fstResult = fstTask.join();
      long secResult = secTask.join();
      sum = fstResult + secResult;
    }
    return sum;
  }
}
