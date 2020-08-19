class A {
  public static void main(String[] a) {
    B b;
    b = new B();
    b.c = new int[12];
    b.c[10] = 12;
    System.out.println(b.c[12]); // error
  }
}

class B extends C {

}

class C {
  int c[];
}