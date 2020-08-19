

class A {
  public static void main(String[] args) {
    B b;
    int c;

    b = new B();
    c = b; // TE: B cannot extend primitives

    System.out.println(c);
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