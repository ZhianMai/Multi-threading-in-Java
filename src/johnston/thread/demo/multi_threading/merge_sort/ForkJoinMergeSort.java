package johnston.thread.demo.multi_threading.merge_sort;

import java.util.Arrays;
import java.util.concurrent.RecursiveTask;

/**
 * Use async FutureTask with fork-join pattern to boost the runtime of merge sort.
 */
public class ForkJoinMergeSort extends RecursiveTask<Void> {
  private int[] array;

  public ForkJoinMergeSort(int[] array) {
    this.array = array;
  }

  @Override
  protected Void compute() {
    if (array.length < 2) {
      return null;
    }

    if (array.length == 2) {
      if (array[0] > array[1]) {
        int temp = array[0];
        array[0] = array[1];
        array[1] = temp;
      }
      return null;
    }

    int mid = array.length / 2;
    int[] leftSubArray = Arrays.copyOfRange(array, 0, mid);
    int[] rightSubArray = Arrays.copyOfRange(array, mid, array.length);
    ForkJoinMergeSort fstTask = new ForkJoinMergeSort(leftSubArray);
    ForkJoinMergeSort secTask = new ForkJoinMergeSort(rightSubArray);

    fstTask.fork();
    secTask.fork();
    fstTask.join();
    secTask.join();

    merge(array, leftSubArray, rightSubArray);
    return null;
  }

  static void merge(int[] result, int[] a, int[] b) {
    int aIdx = 0;
    int bIdx = 0;
    int resIdx = 0;

    while (aIdx < a.length && bIdx < b.length) {
      if (a[aIdx] < b[bIdx]) {
        result[resIdx++] = a[aIdx++];
      } else {
        result[resIdx++] = b[bIdx++];
      }
    }

    while (aIdx < a.length) {
      result[resIdx++] = a[aIdx++];
    }

    while (bIdx < b.length) {
      result[resIdx++] = b[bIdx++];
    }
  }
}
