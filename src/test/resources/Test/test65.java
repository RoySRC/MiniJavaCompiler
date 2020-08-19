

/**
 * The following class typechecks
 */

class A {
  public static void main(String[] args) {
    System.out.println(new B().b(new C()));
  }
}

class B {
  public int b(D d) {
    int tmp;
    tmp = d.d();
    return tmp;
  }
}

class C extends D {

}

class D {
  public int d() {
    return 4;
  }
}