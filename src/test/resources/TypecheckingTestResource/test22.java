

/**
 * The following class type checks
 */

class A {
  public static void main(String[] args) {
    B b;
    int size;

    b = new B();
    size = b.init(1000);

    System.out.println(b.getSize());
  }
}

class B {
  int[] data;
  public int init(int size) {
    data = new int[size];
    return 0;
  }

  public int getSize() {
    return data.length;
  }
}