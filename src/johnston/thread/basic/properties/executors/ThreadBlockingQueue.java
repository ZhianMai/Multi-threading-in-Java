package johnston.thread.basic.properties.executors;

import java.util.Collections;
import java.util.concurrent.*;

/**
 * Blocking queue is a queue that if the size is empty and one thread requires dequeue, then it
 * blocks that thread (wait) until one element is available to dequeue. Each thread pool needs
 * a blocking queue.
 *
 * Blocking queue has several implementations:
 *  - ArrayBlockingQueue: queue implemented by array. The order is FIFO. Size must be defined.
 *
 *  - LinkedBlockingQueue: queue implemented by linked-list. The order is FIFO. Size can be
 *    defined. If not, then the size is unlimited.
 *
 *  - PriorityBlockingQueue: a priority queue that can take in comparator. If no comparator is
 *    provided, then use the natural order of the elements. Size is unlimited.
 *
 *  - DelayQueue: like a blocking queue. Elements can dequeue only if their time is expired. It
 *    can be used in producer-consumer scenario.
 *    The elements stored in DelayQueue must implement Delayed interface. The element that will
 *    expire first will be dequeued first. It cannot dequeue unexpired elements.
 *
 *  - SynchronousQueue: a queue with size 1. This queue allows two threads exchange data
 *    thread-safely.
 *    - take(): if the queue has no data, then the caller will be waiting until data available.
 *    - poll(): like take(), but if no data available, then it returns null instead of waiting.
 *    - put(E e): the caller has to wait until another thread takes away the data.
 *    - offer(E e): enqueue data only if another thread is waiting for data.
 *    -isEmpty(): ALWAYS return true!
 *
 * This demo contains usage of four types of BlockingQueue implementations.
 */
public class ThreadBlockingQueue {
  private static final int DEFAULT_QUEUE_SIZE = 2;
  private static final int DEFAULT_THREAD_POOL_SIZE = 2;
  private static final int DEFAULT_SLEEP_MILLIS_SECOND = 10;
  private static final int ELEMENTS_TO_INSERT_AMOUNT = 10;
  private static final int SLEEP_MILLIS_SEC = 100;

  private static BlockingQueue<Integer> blockingQueue;

  static class GetterThread extends Thread {
    private boolean isTake;

    public GetterThread(String name, boolean isTake) {
      super(name);
      this.isTake = isTake;
    }

    public void run() {
      try {
        Integer result;
        if (isTake) {
          result = blockingQueue.take();
        } else {
          result = blockingQueue.poll();
        }
        print(this.getName() + " got an element from blockingQueue: " + result +
            "\n");
      } catch (InterruptedException e) {
        print(this.getName() + " is interrupted. Stop waiting and exit.\n");
        return;
      }
    }
  }

  static class PutterThread extends Thread {
    private boolean isPut;

    public PutterThread(String name, boolean isPut) {
      super(name);
      this.isPut = isPut;
    }

    public void run() {
      try {
        if (isPut) {
          blockingQueue.put(1);
          print(this.getName() + " put an element to blockingQueue.\n");
        } else {
          print("Offered an element to queue: " + blockingQueue.offer(1));
        }
      } catch (InterruptedException e) {
        print(this.getName() + " is interrupted. Stop waiting and exit.\n");
        return;
      }
    }
  }

  /**
   * Objects of this class can put in the delayedQueue.
   *
   * Natural order: ascending on startTime.
   */
  static class DelayElement implements Delayed {
    private String data;
    private long startTime;

    public DelayElement(String data, long delayInMilliSec) {
      this.data = data;
      this.startTime = System.currentTimeMillis() + delayInMilliSec;
    }

    @Override
    public long getDelay(TimeUnit unit) {
      long diff = startTime - System.currentTimeMillis();
      return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
      long compareTime = ((DelayElement) o).startTime;
      if (this.startTime == compareTime) {
        return 0;
      }
      return this.startTime < compareTime ? -1 : 1;
    }

    public String getData() {
      return data;
    }

    public void setData(String data) {
      this.data = data;
    }

    @Override
    public String toString() {
      return "DelayElement{" +
          "data='" + data + '\'' +
          '}';
    }
  }

  private static void useArrayBlockingQueue() throws InterruptedException {
    blockingQueue = new ArrayBlockingQueue<>(DEFAULT_QUEUE_SIZE);
    Thread getterThread = new GetterThread("Getter Thread", true);
    getterThread.start();

    Thread.sleep(DEFAULT_SLEEP_MILLIS_SECOND);

    print("The blockingQueue size: " + blockingQueue.size());
    print("The running getter Thread's state: " + getterThread.getState());
    print("\nAdding an element to the blockingQueue: " + blockingQueue.offer(1));
  }

  private static void usePriorityBlockingQueue() throws InterruptedException {
    PriorityBlockingQueue<Integer> priorityBlockingQueue =
        new PriorityBlockingQueue<>(1, Collections.reverseOrder());
    blockingQueue = priorityBlockingQueue;
    print("BlockPriorityQueue's order is: " + priorityBlockingQueue.comparator() +
        "\n");

    for (int i = 0; i < ELEMENTS_TO_INSERT_AMOUNT; i++) {
      print("Adding an element to priorityBlockingQueue: " + i);
      blockingQueue.offer(i);
    }

    print("\n");

    while (!blockingQueue.isEmpty()) {
      print("Polling an element to priorityBlockingQueue: " + blockingQueue.take());
    }


    Thread getterThread = new GetterThread("Getter Thread", true);
    getterThread.start();
    Thread.sleep(DEFAULT_SLEEP_MILLIS_SECOND);
    print("\nThe blockingQueue size: " + blockingQueue.size());
    print("The running getter Thread's state: " + getterThread.getState());
    getterThread.interrupt();
  }

  private static void useDelayedQueue() throws InterruptedException {
    BlockingQueue<DelayElement> delayQueue = new DelayQueue<>();
    print("\nDelayQueue enqueue: 0 milli sec delay, 100 milli sec delay," +
        " & 200 milli sec delay.\n");
    delayQueue.offer(new DelayElement("Delay 0 milli sec", 0));
    delayQueue.offer(new DelayElement("Delay 100 milli sec", 100));
    delayQueue.offer(new DelayElement("Delay 200 mill sec", 200));

    print("Dequeue now: " + delayQueue.poll().toString());
    print("Dequeue now. Is it null: " + (delayQueue.poll() == null));

    Thread.sleep(SLEEP_MILLIS_SEC);
    print("\nSlept 100 milli sec. Dequeue now: " + delayQueue.poll().toString());
    print("Dequeue now. Is it null: " + (delayQueue.poll() == null));

    Thread.sleep(SLEEP_MILLIS_SEC);
    print("\nSlept 100 milli sec. Dequeue now: " + delayQueue.poll().toString());
    print("Dequeue now. Is it null: " + (delayQueue.poll() == null) + "\n");
  }

  private static void useSynchronousQueue() throws InterruptedException {
    blockingQueue = new SynchronousQueue<>();
    ExecutorService threadPool = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
    // take(): if the queue has no data, then the caller will be waiting until data available.
    Thread takerThread = new GetterThread("Taker thread", true); // use take()
    // put(E e): the caller has to wait until another thread takes away the data.
    Thread putterThread = new PutterThread("Putter thread", true); // use put()
    // poll(): like take(), but if no data available, then it returns null instead of waiting.
    Thread pollerThread = new GetterThread("Poller thread", false); // use poll()
    // offer(E e): enqueue data only if another thread is waiting for data.
    Thread offeredThread = new PutterThread("Offered thread", true); // use offer()

    print("*** take() - put(E e) Demo ***");
    threadPool.submit(takerThread);
    Thread.sleep(SLEEP_MILLIS_SEC);
    print("Sync queue is empty. Taker runs.");
    Thread.sleep(SLEEP_MILLIS_SEC);
    print("Putter runs.");
    threadPool.submit(putterThread);


    threadPool.submit(putterThread);
    Thread.sleep(SLEEP_MILLIS_SEC);
    print("Sync queue is empty. Putter runs.");
    print("Taker runs.");
    threadPool.submit(takerThread);
    Thread.sleep(SLEEP_MILLIS_SEC);

    print("*** poll() - offer(E e) demo ***");
    print("Sync queue is empty. Poller runs.");
    threadPool.submit(pollerThread);
    Thread.sleep(SLEEP_MILLIS_SEC);
    print("Offerer runs.");
    threadPool.submit(offeredThread);
    Thread.sleep(SLEEP_MILLIS_SEC);
    print("Sync queue is empty. Offerer runs.");
    Thread.sleep(SLEEP_MILLIS_SEC);
    print("Poller runs.");
    threadPool.submit(pollerThread);

    threadPool.shutdown();
  }

  private static void print(String s) {
    System.out.println(s);
  }

  public static void main(String[] args) throws InterruptedException {
    useArrayBlockingQueue();
    //usePriorityBlockingQueue();
    //useDelayedQueue();
    //useSynchronousQueue();
  }
}
