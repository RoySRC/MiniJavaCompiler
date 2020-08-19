

/**
 * The following does not type check
 */

class A {
  public static void main(String[] args) {
    B b;
    b = new B();
    System.out.println(b.init());
  }
}

class B {
  int a;
  public int init() {
    a = 12;
    while ( num < a ) { // TE : num does not exist
      a = a - 1;
    }
    return a;
  }
}