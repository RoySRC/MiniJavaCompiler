

class A {
  public static void main(String[] args) {
    B b;
    C c;

    b = new B();
    c = b;

    System.out.println(c.c());
  }
}

class B extends C {
  int b;
  public int b() {
    b = 123456;
    return b;
  }
}

class C {
  int c;
  public int c() {
    c = 789;
    return c;
  }
}