package johnston.thread.demo.multi_threading;

import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Given coordinates of two vector, calculate their angle. This is achieved by:
 * arcs(beta) = a * b / (|a| * |b|).
 *
 * This calculation is computationally heavy task.
 */
public class AngleBetweenTwoVectors {
  private static final int DEFAULT_VECTOR_LENGTH = 10000;
  private static final int DEFAULT_VECTOR_ENTRY_MIN = -10;
  private static final int DEFAULT_VECTOR_ENTRY_MAX = 10;
  private static final int DEFAULT_THREAD_AMOUNT = 4;
  private static final int DEFAULT_CPU_CORE_AMOUNT = 4;

  private static Random random;
  private int threadAmount = DEFAULT_THREAD_AMOUNT;
  private int cpuCoreAmount = DEFAULT_CPU_CORE_AMOUNT;

  public static double[] getRandomVector(int length, double entryMin, double entryMax) {
    double[] result = new double[length];

    for (int i = 0; i < result.length; i++) {
      result[i] = getRandom(entryMin, entryMax);
    }
    return result;
  }

  private static double getRandom(double minVal, double maxVal) {
    if (maxVal < minVal) {
      throw new ArithmeticException("Max val is smaller than min val");
    }

    if (random == null) {
      // Ignore the data racing here, since it won't breaking anything...
      random = new Random(System.currentTimeMillis());
    }

    return random.nextDouble() * (maxVal - minVal) + minVal;
  }

  public static double getAngleBetweenTwoVectors(double[] vecA, double[] vecB) {
    if (vecA.length != vecB.length) {
      throw new ArithmeticException("Two vectors have different length.");
    }

    double aLengthSquare = 0;
    double bLengthSquare = 0;
    double dotProduct = 0;

    for (int i = 0; i < vecA.length; i++) {
      dotProduct += (vecA[i] * vecB[i]);
      aLengthSquare += (vecA[i] * vecA[i]);
      bLengthSquare += (vecB[i] * vecB[i]);
    }

    double aLength = Math.sqrt(aLengthSquare);
    double bLength = Math.sqrt(bLengthSquare);

    return Math.toDegrees(Math.acos(dotProduct / aLength / bLength));
  }

  static class CalculateAngleBetweenTwoVectorThread extends Thread {
    private double[] vecA;
    private double[] vecB;
    private int beginEntry = 0;
    private int subTaskEntries;
    private double dotProduct = 0;
    private double aLengthSquare = 0;
    private double bLengthSquare = 0;
    private boolean finished = false;

    public CalculateAngleBetweenTwoVectorThread(String name, double[] vecA, double[] vecB,
                                                int subTaskEntries) {
      super(name);
      this.vecA = vecA;
      this.vecB = vecB;
      this.subTaskEntries = subTaskEntries;
    }

    private synchronized int[] getRange() {
      if (beginEntry == vecA.length) {
        return null;
      }
      int begin = beginEntry;
      int range = Math.min(subTaskEntries, vecA.length - beginEntry);
      beginEntry += range;
      return new int[]{begin, begin + range};
    }

    private synchronized void addToDotProduct(double sum) {
      dotProduct += sum;
    }

    private synchronized void addToALengthSquare(double sum) {
      aLengthSquare += sum;
    }

    private synchronized void addToBLengthSquare(double sum) {
      bLengthSquare += sum;
    }

    @Override
    public void run() {
      int[] range = getRange();
      double vecATempSum = 0;
      double vecBTempSum = 0;
      double tempDotProduct = 0;

      for (int i = range[0]; i < range[1]; i++) {
        vecATempSum += vecA[i] * vecA[i];
        vecBTempSum += vecB[i] * vecB[i];
        tempDotProduct += vecA[i] * vecB[i];
      }

      addToDotProduct(tempDotProduct);
      addToALengthSquare(vecATempSum);
      addToBLengthSquare(vecBTempSum);

      if (range[1] == vecA.length) {
        finished = true;
      }
    }

    public double getAngle() throws IllegalAccessError {
      if (!finished) {
        throw new IllegalAccessError("Not finished yet.");
      }
      double aLength = Math.sqrt(aLengthSquare);
      double bLength = Math.sqrt(bLengthSquare);
      return Math.toDegrees(Math.acos(dotProduct / aLength / bLength));
    }
  }

  public double getAngleBetweenTwoVectorsMultiThreaded(double[] vecA, double[] vecB)
      throws InterruptedException {
    if (vecA.length != vecB.length) {
      throw new ArithmeticException("Two vectors have different length.");
    }

    int subTaskEntries = (vecA.length + 1) / threadAmount;
    CalculateAngleBetweenTwoVectorThread calculationThread =
        new CalculateAngleBetweenTwoVectorThread("Calculation Thread", vecA, vecB, subTaskEntries);
    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        cpuCoreAmount,
        cpuCoreAmount * 2,
        10,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(threadAmount),
        new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
    );

    for (int i = 0; i < threadAmount; i++) {
      threadPool.execute(calculationThread);
    }

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.HOURS);
    return calculationThread.getAngle();
  }

  public static void main(String[] args) throws InterruptedException {
    int vecLength = 150000000;
    double vecEntryMin = -189.99495173784872e-50;
    double vecEntryMax = 189.99495173784872e-50;

    double[] vecA = getRandomVector(vecLength, vecEntryMin, vecEntryMax);
    double[] vecB = getRandomVector(vecLength, vecEntryMin, vecEntryMax);
    long startTime = System.currentTimeMillis();
    double resultA = getAngleBetweenTwoVectors(vecA, vecB);
    long totalTime = (System.currentTimeMillis() - startTime);
    System.out.println("Single thread runtime: " + totalTime + "sec.");

    AngleBetweenTwoVectors angleBetweenTwoVectors = new AngleBetweenTwoVectors();
    startTime = System.currentTimeMillis();
    double resultB = angleBetweenTwoVectors.getAngleBetweenTwoVectorsMultiThreaded(vecA, vecB);
    totalTime = (System.currentTimeMillis() - startTime);
    System.out.println("Multi thread runtime: " + totalTime + "sec.");

    System.out.println("Result A: " + resultA);
    System.out.println("Result B: " + resultB);
    System.out.println("Is the result correct: " + (Math.abs(resultA - resultB) < 0.001));
  }
}
