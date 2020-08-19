class A {
  public static void main(String[] a) {
    C c;
    B b;

    c = new C();
    b = new B();

    System.out.println(c.get());
  }
}

class B {
  public int get() {return 1;}
}

class C {
  public int get() {return 2;}
}