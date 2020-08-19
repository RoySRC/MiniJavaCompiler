

/**
 * The following class typechecks
 */

class A {
  public static void main(String[] args) {
    B b;
    b = new B();
    System.out.println(b.c());
  }
}

class B extends C {
  public int b() {
    c = 12;
    return c;
  }
}

class C {
  int c;
  public int c() {
    c = 24;
    return c;
  }
}
