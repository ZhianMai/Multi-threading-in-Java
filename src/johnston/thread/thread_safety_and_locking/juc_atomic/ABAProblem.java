package johnston.thread.thread_safety_and_locking.juc_atomic;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The ABA problem can happen in multi-threading synchronization. When thread A reads the shared
 * value twice and got the same value, and thread A considers this value has not changed. However,
 * during these two reads, another thread B had changed the value and then change it back. Thread
 * A cannot notice this changes and still perform executions.
 *
 * This demo shows the effect of ABA problem in linked list operation. Thread A perform remove head
 * operation, and thread B perform modify the head.next operation. The result shows that all
 * remaining elements of the linked list are lost even though thread A performs "atomic check".
 */
public class ABAProblem {
  static class ListNode {
    public int val;
    public ListNode next;

    public ListNode(int val) {
      this.val =val;
    }
  }

  private final AtomicReference<ListNode> LINKED_LIST_HEAD =
      new AtomicReference<>(new ListNode(0));

  public static void main(String[] args) throws InterruptedException {
    ABAProblem aba = new ABAProblem();

    ListNode head = aba.LINKED_LIST_HEAD.get();

    for (int i = 1; i < 5; i++) {
      head.next = new ListNode(i);
      head = head.next;
    }
    // 0 -> 1 -> 2 -> 3 -> 4

    Thread removeFirst = new Thread(() -> {
      ListNode currHead = aba.LINKED_LIST_HEAD.get();
      ListNode next = currHead.next;

      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      aba.LINKED_LIST_HEAD.compareAndSet(currHead, next); // 1 -> 2 -> 3 -> 4
    });

    Thread replaceSecond = new Thread(() -> {
      ListNode currHead = aba.LINKED_LIST_HEAD.get();
      ListNode newNext = new ListNode(9);
      newNext.next = currHead.next.next;
      currHead.next.next = null;
      currHead.next = newNext; // 0 -> 9 -> 2 -> 3 -> 4
    });

    removeFirst.start();
    replaceSecond.start();
    removeFirst.join();
    replaceSecond.join();

    // Expect: 9 -> 2 -> 3 -> 4
    System.out.print("The current linked list is: ");
    head = aba.LINKED_LIST_HEAD.get();

    while (head != null) {
      System.out.print(head.val + " -> ");
      head = head.next;
    }
    System.out.println("null");
  }
}
