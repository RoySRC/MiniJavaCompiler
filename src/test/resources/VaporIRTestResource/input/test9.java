class A {
  public static void main(String[] a) {
    System.out.println(((new B()).init()).getValue()); // error
  }
}

class B {
  B b;
  int value;
  public B init() {
    return b;
  }
  public int getValue() {
    return value;
  }
}