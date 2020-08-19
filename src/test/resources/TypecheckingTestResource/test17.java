

/**
 * The following class type checks
 */

class A {
  public static void main(String[] args) {
    Array A;
    Integer I;
    boolean initializationStatus;

    A = new Array();
    I = new Integer();

    initializationStatus = I.set(12);
    initializationStatus = A.init(I.get()); // TE: init() function takes only one argument
  }
}

class Array {
  int[] data;

  public boolean init(int size) {
    int x;
    int y;
    data = new int[size];
    return true;
  }

  public int get(int index) {
    return data[index];
  }

  public int set(int index, int value) {
    data[index] = value;
    return data[index];
  }
}

class Integer {
  int a;

  public boolean set(int val) {
    a = val;
    return true;
  }

  public int get() {
    return a;
  }
}