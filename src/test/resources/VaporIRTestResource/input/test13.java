class test13 {
  public static void main(String[] a) {
    A a;
    a = new A();
    System.out.println((a.getB()).setValue(12)); // 12
    System.out.println(a.getU()); // 90
    System.out.println((a.getB()).getValue()); // 12
    System.out.println((a.getB()).setValue(a.getU())); // 90
  }
}

class A extends B {
  int u;
  public B getB() {
    u = 90;
    return this;
  }
  public int getU() {
    return u;
  }
}

class B {
  int value;
  public int setValue(int v) {
    value = v;
    return value;
  }
  public int getValue() {
    return value;
  }
}