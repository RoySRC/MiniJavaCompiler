

/**
 * The following class typechecks
 */

class A {
  public static void main (String[] args) {
    B b;
    b = new B();
    System.out.println(b.max(10, 90));
  }
}

class B {
  public int max(int num1, int num2) {
    int retval;
    if (num1 < num2) {
      retval = num2;
    }
    else {
      retval = num1;
    }
    return retval;
  }
}