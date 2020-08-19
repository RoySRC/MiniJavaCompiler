

/**
 * This test typechecks
 */

class A {
  public static void main(String[] args) {
    C c;
    c = new B().b();
  }
}

class B extends C {
  int b;
  public C b() {
    B retval;

    b = 123456;
    retval = new B();

    return retval;  // implicitly cast B to C
  }
}

class C {
  int c;
  public int c() {
    c = 789;
    return c;
  }
}