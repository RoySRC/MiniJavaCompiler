

/**
 * The following class does not type check
 */

class A {
  public static void main(String[] args) {
    Array A;
    boolean initializationStatus;

    A = new Array();

    initializationStatus = A.init(); // TE: init() function takes only one argument
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