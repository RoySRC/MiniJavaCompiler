

/**
 * Does not typecheck
 */

class A {
  public static void main(String[] args) {
    B b;
    b = new C(); // TE: trying to cast C to B
  }
}

// TODO: C cannot be cast to B, but B can be cast to C

class B extends C {
  int b;
  public int b() {
    b = 123456;
    return b;
  }
}

class C {
  int c;
  public int c() {
    c = 789;
    return c;
  }
}