package johnston.thread.concurrency.design_pattern;

/**
 * This class ensures init is lazy, atomic, and only once.
 */
public class StaticInnerClassSingleton {

  private static class InstanceHolder{
    private static final StaticInnerClassSingleton INSTANCE = new StaticInnerClassSingleton();
  }

  private StaticInnerClassSingleton() {
    System.out.println("Instance init");
  }

  public static final StaticInnerClassSingleton getInstance() {
    return InstanceHolder.INSTANCE;
  }

  public static void printInfo() {
    System.out.println("Singleton class");
  }

  public static void main(String[] args) {
    StaticInnerClassSingleton.printInfo();
    StaticInnerClassSingleton.getInstance();
  }
}
