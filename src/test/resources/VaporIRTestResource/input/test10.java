class A {
  public static void main(String[] a) {
    System.out.println(((new B()).init()).getValue()); // 0
  }
}

class B {
  B b;
  int value;
  public B init() {
    int garbage ;
    b = new B();
    garbage = b.setValue((0-12));
    return b;
  }
  public int getValue() {
    return value;
  }
  public int setValue(int value) {
    value = value;
    return value;
  }
}