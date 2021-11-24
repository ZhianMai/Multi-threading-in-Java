package johnston.thread.demo.multi_threading.three_sum_guava;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreeSumTest {
  @Test
  @DisplayName("Test random unique array generator")
  public void testRandomArrayGenerate() {
    int[] randomArray = ThreeSumUtil.getRandomArrayUnique(20000);
    Set<Integer> set = new HashSet<>();

    for (int i : randomArray) {
      assertTrue(!set.contains(i));
      set.add(i);
    }
  }

  @Test
  @DisplayName("Three sum correctness")
  public void testThreeSumCorrectness() {
    int[] array = ThreeSumUtil.getRandomArrayUnique(20000);
    ThreeSumGuava threeSumGuava = new ThreeSumGuava();
    int result = threeSumGuava.threeSum(array, 9);
    assertEquals(result, ThreeSumUtil.simpleThreeSum(array, 9));
  }

  @Test
  @DisplayName("Performance Comparison")
  public void performanceCompare() {
    int[] array = ThreeSumUtil.getRandomArrayUnique(20000);
    long beginTime = System.currentTimeMillis();
    ThreeSumGuava threeSumGuava = new ThreeSumGuava();
    int result = threeSumGuava.threeSum(array, 9);
    System.out.println("Guava callbace time taken: " + (System.currentTimeMillis() - beginTime));

    beginTime = System.currentTimeMillis();
    int solution = ThreeSumUtil.simpleThreeSum(array, 9);
    System.out.println("Benchmark time taken: " + (System.currentTimeMillis() - beginTime));
    assertEquals(result, solution);
  }
}
