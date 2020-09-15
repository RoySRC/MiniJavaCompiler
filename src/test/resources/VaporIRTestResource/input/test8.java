class A {
  public static void main(String[] a) {
    int result;
    B b;

    b = new B();
    result = b.set(10);

    System.out.println(b.get()); // 10
  }
}

class B {
  int value;
  public int set(int value) {
    value = value;
    return value;
  }
  public int get() {
    return value;
  }
}