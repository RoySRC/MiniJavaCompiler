

/**
 * This class does not typeckeck
 */

class A {
  public static void main(String[] args) {
    B b;
    b = new B().b(); // TE: trying to cast C to B
  }
}

class B extends C {
  int b;
  public C b() {
    B retval;

    b = 123456;
    retval = new B();

    return retval; // implicitly cast B to C
  }
}

class C {
  int c;
  public int c() {
    c = 789;
    return c;
  }
}