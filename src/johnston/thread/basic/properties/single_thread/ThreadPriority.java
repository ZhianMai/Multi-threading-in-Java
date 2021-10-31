package johnston.thread.basic.properties.single_thread;

/**
 * Setting thread priority to guarantees all threads run in order. To set thread threadA priority,
 * call threadA.setPriority(priorityVal)
 *
 * Priority range: 1 (lowest) - 10 (highest).
 *
 * In this class, 10 threads will be created, and each of them will run a heavy math task:
 * calculate the standard deviation of one million double number.
 *
 * The last thread is set to have highest priority, while the others are set to have lowest
 * priority, so the last thread always finishes first.
 */
public class ThreadPriority {
  private static final int THREAD_COUNT = 10;
  private static final int HEAVY_WORK_ROUNDS = 1000000;

  /**
   *  Calculate the standard deviation of a set of random double number.
   *
   * @param rounds how many random double number to calculate
   */
  private static double heavyTask(int rounds) {
    double[] nums = new double[rounds];
    for (int i = 0; i < nums.length; i++) {
      nums[i] = Math.random();
    }
    double result = 0.0;

    for (double i : nums) {
      result += (i * i);
    }
    return Math.sqrt(result / rounds);
  }

  public static void main(String[] args) {
    Thread[] threadPool = new Thread[THREAD_COUNT];

    for (int i = 0; i < threadPool.length; i++) {
      threadPool[i] = new Thread(new Runnable() {
        @Override
        public void run() {
          heavyTask(HEAVY_WORK_ROUNDS);
          System.out.print("Finished running thread: " + Thread.currentThread().getName());
          System.out.println(", priority: " + Thread.currentThread().getPriority());
        }
      });
      threadPool[i].setName(String.valueOf(i));
      // Set the last thread has the highest priority, and the others have the lowest.
      int priority = (i == THREAD_COUNT - 1 ? Thread.MAX_PRIORITY : Thread.MIN_PRIORITY);
      threadPool[i].setPriority(priority);
    }

    for (Thread thread : threadPool) {
      thread.start();
    }
  }
}
