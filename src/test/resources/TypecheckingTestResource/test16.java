

/**
 * The following class does not type check
 */

class A {
  public static void main(String[] args) {
    Array A;
    Integer I;
    boolean initializationStatus;

    A = new Array();
    I = new Integer();

    initializationStatus = A.init(I, 13, 14); // TE: init() function takes only one argument
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