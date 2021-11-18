package johnston.thread.communications;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Util class ThreadMXBean can detect existing threads trapped in deadlock and return their thread
 * id. In this demo I first created two threads trapped in deadlock, then use ThreadMXBean to
 * locate and resolve their deadlock, letting them finish execution correctly.
 *
 * However, the best way is to locate the deadlock before it happens!
 */
public class DeadLockResolution {
  private static final ReentrantLock LOCK_A = new ReentrantLock();
  private static final ReentrantLock LOCK_B= new ReentrantLock();
  private static ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();

  private void createDeadLock() throws InterruptedException {
    Thread threadA = new Thread(()->{
      while (true) {
        try {
          LOCK_A.lockInterruptibly();
          System.out.println("Thread A holds LOCK_A.");
          Thread.sleep(10);

          LOCK_B.lockInterruptibly();
          System.out.println("Thread A holds LOCK_A & LOCK_B.");
          return;
        } catch (InterruptedException e) {
          System.out.println("Thread A interrupted, restart");
        } finally {
          if (LOCK_A.isHeldByCurrentThread()) {
            LOCK_A.unlock();
          }
          if (LOCK_B.isHeldByCurrentThread()) {
            LOCK_B.unlock();
          }

          try {
            Thread.sleep(10); // Let other thread hold the lock.
          } catch (InterruptedException e) {
            // Do nothing
          }
        }
      }
    }, "ThreadA");

    Thread threadB = new Thread(()->{
      while (true) {
        try {
          LOCK_B.lockInterruptibly();
          System.out.println("Thread B holds LOCK_B.");
          Thread.sleep(10);

          LOCK_A.lockInterruptibly();
          System.out.println("Thread B holds LOCK_A & LOCK_B.");
          return;
        } catch (InterruptedException e) {
          System.out.println("Thread B interrupted, restart");
        } finally {
          if (LOCK_A.isHeldByCurrentThread()) {
            LOCK_A.unlock();
          }
          if (LOCK_B.isHeldByCurrentThread()) {
            LOCK_B.unlock();
          }
          try {
            Thread.sleep(10); // Let other thread hold the lock.
          } catch (InterruptedException e) {
            // Do nothing
          }
        }
      }
    }, "ThreadB");

    threadA.start();
    threadB.start();
    Thread.sleep(10); // Wait for deadlock
  }

  private static void interruptDeadLock() throws InterruptedException {
    long[] deadLockThreadId = mxBean.findDeadlockedThreads();
    while (deadLockThreadId != null) {
      for (Thread t : Thread.getAllStackTraces().keySet()) {
        if (t.getId() == deadLockThreadId[0]) {
          t.interrupt();
        }
      }
      deadLockThreadId = mxBean.findDeadlockedThreads();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    DeadLockResolution deadLockInterruption = new DeadLockResolution();
    deadLockInterruption.createDeadLock();
    Thread.sleep(10);
    interruptDeadLock();
  }
}
