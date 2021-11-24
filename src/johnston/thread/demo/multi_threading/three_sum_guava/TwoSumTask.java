package johnston.thread.demo.multi_threading.three_sum_guava;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class TwoSumTask implements Callable<Integer> {
  private final int[] INPUT;
  private final int TWO_SUM_TARGET[];
  private final int TARGET_BEGIN;
  private final int TARGET_END;

  public TwoSumTask(int[] INPUT, int[] TWO_SUM_TARGET, int TARGET_BEGIN, int TARGET_END) {
    this.INPUT = INPUT;
    this.TWO_SUM_TARGET = TWO_SUM_TARGET;
    this.TARGET_BEGIN = TARGET_BEGIN;
    this.TARGET_END = TARGET_END;
  }

  @Override
  public Integer call() throws Exception {
    int twoSumCount = 0;
    Set<Integer> hashSet = new HashSet<>();

    for (int i = TARGET_BEGIN; i < TARGET_END; i++) {
      int twoSumTarget = TWO_SUM_TARGET[i];
      hashSet.clear();

      for (int num : INPUT) {
        if (hashSet.contains(twoSumTarget - num)) {
          twoSumCount++;
        }
        hashSet.add(num);
      }
    }
    return twoSumCount;
  }
}
