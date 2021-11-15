package johnston.thread.thread_safety_and_locking.juc_atomic;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Using AtomicStampedReference to flag the inconsistency of the synced object can avoid ABA
 * problem. It requires that every operation on the stamped reference should update the version
 * variable as well.
 *
 * AtomicMarkableReference is a simplified AtomicStampedReference. It can only tell the variable is
 * modified once or not.
 */
public class StampedRefNoABA {
  static class ListNode {
    public int val;
    public ListNode next;

    public ListNode(int val) {
      this.val =val;
    }
  }

  private final AtomicStampedReference<ListNode> LINKED_LIST_HEAD =
      new AtomicStampedReference<>(new ListNode(0), 0);

  public static void main(String[] args) throws InterruptedException {
    StampedRefNoABA aba = new StampedRefNoABA();

    ListNode head = aba.LINKED_LIST_HEAD.getReference();

    for (int i = 1; i < 5; i++) {
      head.next = new ListNode(i);
      head = head.next;
    }
    // 0 -> 1 -> 2 -> 3 -> 4

    Thread removeFirst = new Thread(() -> {
      ListNode currHead = aba.LINKED_LIST_HEAD.getReference();
      int prevStamp = aba.LINKED_LIST_HEAD.getStamp();
      ListNode next = currHead.next;

      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      // 1 -> 2 -> 3 -> 4
      boolean result = aba.LINKED_LIST_HEAD.compareAndSet(currHead, next,
          prevStamp, prevStamp + 1);

      if (!result) {
        throw new RuntimeException("Inconsistency found. Remove first element rejected.");
      }
    });

    Thread replaceSecond = new Thread(() -> {
      ListNode currHead = aba.LINKED_LIST_HEAD.getReference();
      int prevStamp = aba.LINKED_LIST_HEAD.getStamp();
      ListNode newNext = new ListNode(9);
      newNext.next = currHead.next.next;
      currHead.next.next = null;
      currHead.next = newNext; // 0 -> 9 -> 2 -> 3 -> 4
      boolean result =
          aba.LINKED_LIST_HEAD.compareAndSet(currHead, currHead, prevStamp, prevStamp + 1);

      if (!result) {
        throw new RuntimeException("Inconsistency found. Replace second element rejected.");
      }
    });

    removeFirst.start();
    replaceSecond.start();
    removeFirst.join();
    replaceSecond.join();

    // Expect: 9 -> 2 -> 3 -> 4
    System.out.print("The current linked list is: ");
    head = aba.LINKED_LIST_HEAD.getReference();

    while (head != null) {
      System.out.print(head.val + " -> ");
      head = head.next;
    }
    System.out.println("null");
  }
}
