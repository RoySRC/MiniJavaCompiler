

class A {
  public static void main(String[] args) {
    C c;
    c = new B().b();
  }
}

class B extends C {
  int b;
  public BC b() {
    B retval;

    b = 123456;
    retval = new B();

    return retval;  // TE: BC does not exist
  }
}

class C {
  int c;
  public int c() {
    c = 789;
    return c;
  }
}