

/**
 * The following class does not typecheck
 */

class A {
  public static void main(String[] args) {
    System.out.println(new B().b(new D()));
  }
}

class B {
  public int b(C c) {
    int tmp;
    tmp = c.c();
    return tmp;
  }
}

class C extends D {
  public int c() {
    return this.d();
  }
}

class D {
  public int d() {
    return 4;
  }
}