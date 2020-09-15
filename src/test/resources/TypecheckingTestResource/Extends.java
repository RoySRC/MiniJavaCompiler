class B {

  public static void main(String[] args) {
//    A a;
//    int v;
//    int[] kk;
//
//    v = 12;
//    {a = new A();}
//    v = a.x();
//    kk = new int[12];
//
//    if ((false && true)) {}else{}
//    if (1 < 2) {}else{}
//
    System.out.println(12+2);
  }

}

class C {

  int z;

  public int y(int a) {
    return a;
  }

}

class D {
  public int getName() {
    return 9;
  }
}

class A extends C {

  D d;
  int y;

  public int y() { return 99; }

  public  int x() {
    int l;
    int m;
    int y;

    d = new D();
    l = this.y();
    m = this.y(9);

    return l;
  }

}

