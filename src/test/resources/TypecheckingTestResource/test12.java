

/**
 * The following class does not typecheck
 */

class A {
  public static void main(String[] args) {
    B b;
    b = new B();
    System.out.println(b.c()); // TE, function c() is not defined in class B or C
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
}
