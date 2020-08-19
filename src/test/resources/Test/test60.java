

class A {
  public static void main(String[] args) {
    C c;

    b = new B(); // TE: identifier b not found

    System.out.println(b.b());
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