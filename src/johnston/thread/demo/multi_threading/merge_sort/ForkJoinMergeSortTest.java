package johnston.thread.demo.multi_threading.merge_sort;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;

import static johnston.thread.demo.multi_threading.merge_sort.ForkJoinMergeSort.merge;

public class ForkJoinMergeSortTest {
  public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
    int[] input = new int[100000000];
    Random rand = new Random();

    for (int i = 0; i < input.length; i++) {
      input[i] = rand.nextInt();
    }

    Long startMin = System.currentTimeMillis();
    ForkJoinMergeSort mergesortTask = new ForkJoinMergeSort(input);
    ForkJoinPool forkJoinPool = new ForkJoinPool();
    Future<Void> future =forkJoinPool.submit(mergesortTask);
    future.get(1, TimeUnit.HOURS);
    System.out.println("Fork-Join time taken: " + ((System.currentTimeMillis() - startMin)) + " ms");
    checkSorted(input);

    for (int i = 0; i < input.length; i++) {
      input[i] = rand.nextInt();
    }
    mergeSortBenchmark(input);
  }

  private static void mergeSortBenchmark(int[] input) {
    Long startMin = System.currentTimeMillis();
    simpleMergeSort(input);
    System.out.println("Single-threaded benchmark time taken: " +
        ((System.currentTimeMillis() - startMin)) + " ms");
    checkSorted(input);
  }

  private static void simpleMergeSort(int[] array) {
    if (array.length < 2) {
      return;
    }

    if (array.length == 2) {
      if (array[0] > array[1]) {
        int temp = array[0];
        array[0] = array[1];
        array[1] = temp;
      }
      return;
    }

    int mid = array.length / 2;
    int[] leftSubArray = Arrays.copyOfRange(array, 0, mid);
    int[] rightSubArray = Arrays.copyOfRange(array, mid, array.length);
    simpleMergeSort(leftSubArray);
    simpleMergeSort(rightSubArray);
    merge(array, leftSubArray, rightSubArray);
  }

  private static void checkSorted(int[] sorted) {
    for (int i = 1; i < sorted.length; i++) {
      if (sorted[i - 1] > sorted[i]) {
        System.out.println("Unsorted, idx: " + i + ", left & right: " + sorted[i - 1]+ "," + sorted[i]);
      }
    }
    System.out.println("Sorted");
  }
}
