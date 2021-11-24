package johnston.thread.demo.multi_threading.three_sum_guava;

import java.util.Arrays;
import java.util.Random;

public class ThreeSumUtil {
  private static Random random = new Random();

  public static int[] getRandomArrayUnique(int length) {
    int[] result = new int[length];
    int deltaRandomRange = Integer.MAX_VALUE / length;
    int currVal = Integer.MIN_VALUE + 1;

    // Generate sorted random array. All elements are unique.
    for (int i = 0; i < length; i++) {
      result[i] = currVal;
      currVal += (random.nextInt(deltaRandomRange) + 1);
    }

    // Shuffle sorted array
    for (int i = length - 1; i > 0; i--) {
      int randomIdx = random.nextInt(i + 1);
      int temp = result[randomIdx];
      result[randomIdx] = result[i];
      result[i] = temp;
    }

    return result;
  }

  public static int[] getTwoSumTarget(int[] array, int threeSumTarget) {
    int[] result = new int[array.length];
    for (int i = 0; i < array.length; i++) {
      result[i] = threeSumTarget - array[i];
    }
    return result;
  }

  public static int simpleThreeSum(int[] nums, int target) {
    int result = 0;
    Arrays.sort(nums);

    for (int i = 0; i < nums.length - 2; i++) {
      if (i > 0 && nums[i] == nums[i - 1]) {
        continue;
      }

      int left = i + 1;
      int right = nums.length - 1;

      while (left < right) {
        int twoSum = nums[left] + nums[right];

        if (nums[i] + twoSum == target) {
          result++;
          left++;

          while (left < right && nums[left] == nums[left - 1]) {
            left++;
          }
        } else if (twoSum + nums[i] < 0) {
          left++;
        } else {
          right--;
        }
      }
    }
    return result;
  }
}
