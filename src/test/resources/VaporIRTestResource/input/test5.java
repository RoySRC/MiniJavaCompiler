class A {
  public static void main(String[] a) {
    B b;
    b = new B();
    if ((b.setA(12)) < 10)
      System.out.println(1);
    else
      System.out.println(2);
  }
}

class B {
  int a;
  public int setA(int v) {
    a = v;
    return a;
  }
}