class test12 {
  public static void main(String[] args) {
    B b;
    int i;
    int[] a;

    i = 1;
    b = new B();
    a = new int[2000];
    while (!((a.length) < i)) { // a.length >= i
      a[(i-1)] = (i*2);
      i = i+1;
    }
    System.out.println(9999);

    System.out.println(b.setA(a)); // 20000
    System.out.println((b.getA())[1]); // 4
  }
}

class B {
  int[] a;
  public int setA(int[] a) {
    a = new int[((a.length)-1)];
    return a.length;
  }
  public int[] getA() {
    return a;
  }
}