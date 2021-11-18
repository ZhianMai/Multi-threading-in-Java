package johnston.thread.demo.multi_threading;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * A simple spinlock implementation. Supports lock(), unlock(), and tryLock() method. It uses
 * thread id to record the current thread who got the lock. 
 */
public class SimpleSpinLockImpl implements Lock {
  private AtomicReference<Thread> lockOwner;

  public SimpleSpinLockImpl() {
    lockOwner = new AtomicReference<>(null);
  }

  @Override
  public void lock() {
    Thread currThread = Thread.currentThread();

    while (!lockOwner.compareAndSet(null, currThread)) {
      Thread.yield();
    }
    // System.out.println(currThread.getName() + " got the lock");
  }

  @Override
  public void unlock() {
    Thread currThread = Thread.currentThread();

    if (currThread.equals(lockOwner.get())) {
      lockOwner.set(null);
      // System.out.println(currThread.getName() + " release the lock");
    }
  }

  @Override
  public void lockInterruptibly() throws InterruptedException {
    throw new UnsupportedOperationException("Method lockInterruptibly() not supported");
  }

  @Override
  public boolean tryLock() {
    Thread currThread = Thread.currentThread();

    return lockOwner.compareAndSet(null, currThread);
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    throw new UnsupportedOperationException("Method tryLock(long time) not supported");
  }

  @Override
  public Condition newCondition() {
    throw new UnsupportedOperationException("Method newCondition() not supported");
  }

  @Test
  @DisplayName("Test tryLock after locking")
  public void testTryLock() throws InterruptedException, ExecutionException {
    Lock spinLock = new SimpleSpinLockImpl();

    Callable<Integer> lockCallable = new Callable() {
      @Override
      public Integer call() throws Exception {
        spinLock.lock();
        try {
          return 1;
        } finally {
          spinLock.unlock();
        }
      }
    };

    Callable<Boolean> tryLockCallable = new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return tryLock();
      }
    };

    FutureTask<Integer> lockTask = new FutureTask<>(lockCallable);
    FutureTask<Boolean> tryLockTask = new FutureTask<>(tryLockCallable);
    Thread lockThread = new Thread(lockTask);
    Thread tryLockThread = new Thread(tryLockTask);
    lockThread.start();
    tryLockThread.start();
    Thread.sleep(10);

    assertEquals(1, lockTask.get());
    assertTrue(tryLockTask.get());
  }

  @Test
  @DisplayName("Test Locking")
  public void testLock() throws InterruptedException, ExecutionException {
    Lock spinLock = new SimpleSpinLockImpl();
    AtomicInteger criticalSection = new AtomicInteger();

    Thread lockFirst = new Thread(() -> {
        spinLock.lock();
        try {
          criticalSection.set(1);
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          spinLock.unlock();
        }
    }, "ThreadA");

    Thread lockAfter = new Thread(() -> {
      spinLock.lock();
      criticalSection.set(-1);
    }, "ThreadB");

    assertEquals(0, criticalSection.get());
    lockFirst.start();
    Thread.sleep(1);
    assertEquals(1, criticalSection.get());
    lockAfter.start();
    Thread.sleep(1);
    assertEquals(1, criticalSection.get());
    Thread.sleep(10); // Wait for threadA to unlock
    assertEquals(-1, criticalSection.get());
  }
}
