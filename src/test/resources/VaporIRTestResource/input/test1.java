class A {
  public static void main(String[] args) {
    B b;
    E e;
    E tmpE;
    int garbage;

    b = new B();
    e = new E();

    System.out.println(b.func()); // 12
    System.out.println(b.func(((((8 - 1) + 3) * 6) - ((3 + 7) * 2)))); // 40
    System.out.println(b.func(false));  // 14

    garbage = e.setValue(16);
    System.out.println(e.getValue()); // 16

    tmpE = e.genE();
    tmpE = e.getE();
    garbage = (e.getE()).setValue(12);
    System.out.println(garbage); // 12
    System.out.println(e.getValue()); // 16

    tmpE = e.getE();
    garbage = (e.getF()).setValue(20);
    System.out.println((e.getF()).getValue()); // 20

    garbage = e.setE(new E());
    System.out.println(garbage);  // 16
    System.out.println(e.getValue()); // 16
    System.out.println((e.getE()).getValue()); // 0
    System.out.println((e.getF()).getValue()); // 0
  }
}

class B extends C {
  public int func() { return 12; }
}

class C extends D {
  public int func(int i) {return i;}
}

class D {
  public int func(boolean k) {
    return 14;
  }
}

class E extends F {
  E e;
  int value;

  public int setValue(int v) {
    value = v;
    return value; // should this return int value or this.value
  }
  public E genE() {
    e = new E();
    return e;
  }
  public E getE() {
    return e;
  }
  public int setE(E e_) {
    e = e_;
    return 16;
  }
  public F getF() {
    return e;
  }
  public int getValue() {return value;}

}

class F {
  int value;
  public int setValue(int v) {
    value = v;
    return value; // should this return int value or this.value
  }
  public int getValue() {
    return value;
  }
}