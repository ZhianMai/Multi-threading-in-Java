package johnston.thread.demo.multi_threading;

import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Demo of using multi-threading to calculate matrix multiplication. Use thread pool to create
 * working threads.
 *
 * A * B = C, A_cols == B_rows
 * C[i][j] = sum(A[i][0 - n] * B[0 - n][j])
 */
public class MatrixMultiplication {
  private static final int DEFAULT_MATRIX_A_MIN_ROW = 1000;
  private static final int DEFAULT_MATRIX_B_MIN_COL = 1000;
  private static final int DEFAULT_MIN_A_COLUMN_B_ROW = 1000;
  private static final int DEFAULT_THREAD_AMOUNT;
  private static final int DEFAULT_CPU_CORE_AMOUNT;

  static {
    DEFAULT_CPU_CORE_AMOUNT = Runtime.getRuntime().availableProcessors();
    DEFAULT_THREAD_AMOUNT = DEFAULT_CPU_CORE_AMOUNT;
  }

  private static Random random;
  private int threadAmount = DEFAULT_THREAD_AMOUNT;
  private int cpuCoreAmount = DEFAULT_CPU_CORE_AMOUNT;

  /**
   * Method to calculate matrix multiplication by iterating each entry in one thread.
   */
  public static int[][] multiplyMatrix(int[][] matrixA, int[][] matrixB) {
    validateMatrixMultiply(matrixA[0].length, matrixB.length);

    int aRow = matrixA.length;
    int aColBRow = matrixA[0].length;
    int bCol = matrixB[0].length;
    int[][] result = new int[aRow][bCol];

    for (int i = 0; i < aRow; i++) {
      for (int j = 0; j < bCol; j++) {
        for (int k = 0; k < aColBRow; k++) {
          result[i][j] += (matrixA[i][k] * matrixB[k][j]);
        }
      }
    }
    return result;
  }

  /**
   * Thread class for calculating matrix multiplication.
   */
  static class MatrixMultiplicationThread extends Thread {
    private int entriesAssigned;
    private int[][] matrixA;
    private int[][] matrixB;
    private int[][] result;
    private int totalEntries;
    private int subTaskEntries;
    private int bCol;
    private int aColBRow;

    public MatrixMultiplicationThread(int[][] matrixA, int[][] matrixB, int[][] result,
                                      int subTaskEntries, String name) {
      super(name);
      this.entriesAssigned = 0;
      this.matrixA = matrixA;
      this.matrixB = matrixB;
      this.result = result;
      this.totalEntries = matrixA.length * matrixB[0].length;
      this.subTaskEntries = subTaskEntries;
      bCol = matrixB[0].length;
      aColBRow = matrixB.length;
    }

    /**
     * Assign a range of entries in the result matrix for each thread to calculate.
     * This method is synchronized to make sure each assignment is atomic to ensure
     * computation consistency.
     *
     * @return range[]: range[0]: begin entry, range[1]: end entry exclusively.
     * Return null if no more tasks.
     */
    private synchronized int[] getEntryRange() {
      if (entriesAssigned == this.totalEntries) {
        return null;
      }
      int begin = entriesAssigned;
      int total = Math.min(this.subTaskEntries, this.totalEntries - entriesAssigned);
      entriesAssigned += total;

      return new int[]{begin, begin + total};
    }

    public void run() {
      int[] entryRange = getEntryRange();
      if (entryRange == null) {
        return;
      }

      for (int i = entryRange[0]; i < entryRange[1]; i++) {
        int row = i / bCol;
        int col = i % bCol;

        for (int j = 0; j < aColBRow; j++) {
          this.result[row][col] += (this.matrixA[row][j] * this.matrixB[j][col]);
        }
      }
    }
  } // End thread class

  /**
   * Method to calculate matrix multiplication using multi-threading. Use thread pool to create
   * threads. The corePoolSize is the CPU core amount set by user.
   */
  public int[][] multiThreadedMultiplyMatrix(int[][] matrixA, int[][] matrixB)
      throws InterruptedException {
    validateMatrixMultiply(matrixA[0].length, matrixB.length);

    int aRow = matrixA.length;
    int bCol = matrixB[0].length;
    int[][] result = new int[aRow][bCol];
    int totalEntries = aRow * bCol;
    // Ceiling computation
    int subTaskEntries = (totalEntries - 1) / threadAmount + 1;

    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        cpuCoreAmount,
        cpuCoreAmount * 2,
        10,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(threadAmount),
        new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
    );

    // Create new Thread object
    Thread calculationThread = new MatrixMultiplicationThread(matrixA, matrixB, result,
        subTaskEntries,"Matrix Multiplication Thread A");

    // Assign tasks
    for (int i = 0; i < threadAmount; i++) {
      threadPool.execute(calculationThread);
    }

    threadPool.shutdown();
    threadPool.awaitTermination(1, TimeUnit.HOURS);
    return result;
  }

  /**
   * Method to generate a matrix whose entries are random number within the given range.
   * @param row The row of the matrix
   * @param col The column of the matrix
   * @param entryMin The lower bound of entry value
   * @param entryMax The upper bound of entry value
   * @return A matrix
   */
  public static int[][] generateMatrix(int row, int col, int entryMin, int entryMax) {
    int[][] matrix = new int[row][col];

    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        matrix[i][j] = getRandom(entryMin, entryMax);
      }
    }
    return matrix;
  }

  private static int getRandom(int minVal, int maxVal) {
    if (maxVal < minVal) {
      throw new ArithmeticException("Max val is smaller than min val");
    }

    if (random == null) {
      // Ignore the data racing here, since it won't breaking anything...
      random = new Random(System.currentTimeMillis());
    }

    return random.nextInt((maxVal - minVal) + 1) + minVal;
  }

  /**
   * Method to check if two given matrices are identical
   */
  public static boolean compareMatrix(int[][] matrixA, int[][] matrixB) {
    if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
      return false;
    }

    for (int i = 0; i < matrixA.length; i++) {
      for (int j = 0; j < matrixA[0].length; j++) {
        if (matrixA[i][j] != matrixB[i][j]) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Print a matrix in column right-aligned manner.
   */
  public static void printMatrix(int[][] matrix, int entryRange) {
    int digit = 2;
    while (entryRange != 0) {
      digit++;
      entryRange /= 10;
    }

    String entryFormat = "%" + digit + "s";

    for (int[] row : matrix) {
      for (int entry : row) {
        System.out.printf(entryFormat, entry);
      }
      System.out.println();
    }
    System.out.println();
  }

  public int getThreadAmount() {
    return threadAmount;
  }

  public void setThreadAmount(int threadAmount) {
    this.threadAmount = threadAmount;
  }

  public int getCpuCoreAmount() {
    return cpuCoreAmount;
  }

  public void setCpuCoreAmount(int cpuCoreAmount) {
    this.cpuCoreAmount = cpuCoreAmount;
  }

  private static void validateMatrixMultiply(int aCol, int bRow) {
    if (aCol != bRow) {
      throw new ArithmeticException("Column of matrixA does not match row of matrixB");
    }
  }

  /**
   * Program entrance.
   */
  public static void main(String[] args) throws InterruptedException {
    MatrixMultiplication matrixMul = new MatrixMultiplication();
    System.out.println(DEFAULT_CPU_CORE_AMOUNT);
    int aRow = DEFAULT_MATRIX_A_MIN_ROW * 2;
    int aColBRow = DEFAULT_MIN_A_COLUMN_B_ROW * 2;
    int bCol = DEFAULT_MATRIX_B_MIN_COL * 2;
    int randomEntryMin = -5;
    int randomEntryMax = 5;
    int range = Math.abs(randomEntryMax) + Math.abs(randomEntryMin);

    int[][] matrixA = matrixMul.generateMatrix(aRow, aColBRow, randomEntryMin, randomEntryMax);
    int[][] matrixB = matrixMul.generateMatrix(aColBRow, bCol, randomEntryMin, randomEntryMax);

    long startTime = System.currentTimeMillis();
    int[][] resultA = matrixMul.multiplyMatrix(matrixA, matrixB);
    long totalTime = (System.currentTimeMillis() - startTime) / 1000;
    System.out.println("Single thread runtime: " + totalTime + "sec.");

    matrixMul.setThreadAmount(5);
    matrixMul.setCpuCoreAmount(5);

    startTime = System.currentTimeMillis();
    int[][] resultB = matrixMul.multiThreadedMultiplyMatrix(matrixA, matrixB);
    totalTime = (System.currentTimeMillis() - startTime) / 1000;
    System.out.println("Multi thread runtime: " + totalTime + "sec.");

    System.out.println("Is the result correct: " + compareMatrix(resultA, resultB));

    // Output:
    // Single thread runtime: 47sec.
    // Multi thread runtime: 11sec.
    // Is the result correct: true
  }
}
