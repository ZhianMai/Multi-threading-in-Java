package johnston.thread.thread_safety_and_locking.juc_atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Atomic filed updater can ensure modifying fields in an object is atomic. The filed to update
 * must be public volatile.
 */
public class AtomicObjectFieldUpdater {
  static class OneInteger {
    public volatile int increment;

    public OneInteger(int increment) {
      this.increment = increment;
    }
  }

  public static void main(String[] args) {
    OneInteger oneInt = new OneInteger(0);

    AtomicIntegerFieldUpdater<OneInteger> updater =
        AtomicIntegerFieldUpdater.newUpdater(OneInteger.class, "increment");
    updater.getAndIncrement(oneInt);
    updater.getAndAdd(oneInt,100);
    System.out.println("New object field value is: " + oneInt.increment);
  }
}
