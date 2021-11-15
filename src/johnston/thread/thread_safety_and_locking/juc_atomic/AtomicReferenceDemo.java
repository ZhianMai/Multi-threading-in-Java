package johnston.thread.thread_safety_and_locking.juc_atomic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * There are three reference types in JUC atomic package: reference, stamped reference, and marked
 * reference. AtomicReference can ensure that referencing the object can always be atomic. The
 * AtomicStampedReference is like adding an integer as version or mark on the object like
 * <Object, Integer>. The AtomicMakrkableReference is like <Object, Boolean>.
 *
 * In Java, assigning variable a value is atomic. The source code shows that the set method of
 * AtomicReference is simply an equal mark, but its lazySet, getAndSet, compareAndSet, and
 * weakCompareAndSet methods guarantee to be atomic since they involve two operations. They use CAS
 * to ensure thread-safety which uses low-level OS call from the package unsafe.
 *
 * This demo shows that getAndSet() method is atomic. There are ten threads which concurrently
 * record the previous version, and update a new version on the atomic reference using
 * getAndSet(). The atomic reference can guarantee that all records return from getAndSet() are
 * consistent.
 *
 * Be careful! Modifying the object referenced by AtomicReference is not atomic!
 */
public class AtomicReferenceDemo {
  private static final int THREAD_INCREMENT_TIMES = 1000000;
  private static final int THREAD_AMOUNT = 10;
  /**
   * Global unique ID distributer.
   */
  private static final AtomicInteger VERSION_DISTRIBUTER = new AtomicInteger(0);
  private static List<Integer>[] versionRecordList;

  private static AtomicReference<OneInteger> atomicRef;
  private static OneInteger nonAtomicInt = new OneInteger(0);

  static class OneInteger {
    public int increment;

    public OneInteger(int increment) {
      this.increment = increment;
    }
  }

  public static void main(String[] args) {
    CountDownLatch countDownLatch = new CountDownLatch(THREAD_AMOUNT);
    OneInteger atomicInt = new OneInteger(0);
    versionRecordList = new List[THREAD_AMOUNT];
    atomicRef = new AtomicReference(atomicInt);

    class IncrementTask implements Runnable {
      private int id;

      public IncrementTask(int id) {
        this.id = id;
        versionRecordList[id] = new LinkedList<>();
      }

      @Override
      public void run() {
        for (int i = 0; i < THREAD_INCREMENT_TIMES; i++) {
          OneInteger prev =
              atomicRef.getAndSet(new OneInteger(VERSION_DISTRIBUTER.incrementAndGet()));
          versionRecordList[id].add(prev.increment);

          /**
           * Non-atomic Reference Comparison
           *
           * Comment the two lines of code above and uncomment the below two lines to test
           * nonatomic reference. It will fail the test.
           */
          // records[id].add(nonAtomicInt.increment);
          // nonAtomicInt = new OneInteger(increment.incrementAndGet());
        }
        countDownLatch.countDown();
      }
    }

    ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
        THREAD_AMOUNT,
        THREAD_AMOUNT * 2,
        10,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingDeque<>(THREAD_AMOUNT),
        new ThreadPoolExecutor.CallerRunsPolicy() // Thread who submit task run task itself.
    );

    for (int i = 0; i < THREAD_AMOUNT; i++) {
      threadPool.execute(new IncrementTask(i));
    }

    threadPool.shutdown();
    try {
      countDownLatch.await(); // Print the result after all threads finished.
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    checkConsistency(versionRecordList);
  }

  private static boolean checkConsistency(List<Integer>[] versionRecordList) {
    List<Integer> record = new ArrayList<>();
    int count = 0;

    for (List<Integer> r : versionRecordList) {
      record.addAll(r);
    }

    Collections.sort(record);

    for (int i = 1; i < record.size(); i++) {
      if (record.get(i) != record.get(i - 1) + 1) {
        count++;
      }
    }

    if (count != 0) {
      System.out.println("Not consistency " + count + " times.");
      return false;
    } else {
      System.out.println("The result is consistency!");
      return true;
    }
  }
}
