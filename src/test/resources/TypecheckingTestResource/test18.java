

/**
 * The following type checks
 */

class A {
  public static void main(String[] args) {
    B b;
    b = new B();
    System.out.println(b.init(0));
  }
}

class B {
  int a;
  public int init(int num) {
    a = 12;
    while ( num < a ) {
      a = a - 1;
    }
    return a;
  }
}