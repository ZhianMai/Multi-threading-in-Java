package johnston.thread.thread_safety_and_locking;

import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public class SyncedDataStructure {
  public static void main(String[] args) {
    Vector<Integer> vector = new Vector<>();
    Stack<Integer> stack = new Stack<>();
    Map<Integer, Integer> table = new Hashtable<>();

    vector.add(1);
    vector.add(2);
    stack.add(1);
    table.put(1,1);
  }
}
