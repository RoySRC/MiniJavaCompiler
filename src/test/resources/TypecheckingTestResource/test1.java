/**
 * This class typechecks
 */

class A {
  public static void main(String[] args) {
    B b;
    b = new B();
    System.out.println(b.a());
  }
}

class B extends C {
  int a;
  public int a() {
    int c;
    c = this.c();
    a = 12 * c;
    return a;
  }
}

class C {
  int c;

  public int c() {
    c = 12;
    return c;
  }
}