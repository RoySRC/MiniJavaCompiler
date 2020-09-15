

/**
 * This class typechecks
 */

class A {
  public static void main(String[] args) {
    B b;
    b = new B();
    System.out.println(b.b());
  }
}

class B extends C {
  public int b() {
    boolean initResult;
    initResult = this.init();
    a[0] = 1;
    return a[0];
  }
}

class C {
  int[] a;
  public boolean init() {
    a = new int[12];
    return true;
  }
}