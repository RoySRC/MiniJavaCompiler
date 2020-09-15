

/**
 * The following class does not typecheck
 */

class A {
  public static void main (String[] args) {
    Array A;
    int initializationStatus;
    int value;
    A = new Array();

    initializationStatus = A.init(12); // TE: trying to assign boolean to integer
    value = A.set(0, 12);

    System.out.println( A.get(0) );
  }
}

class Array {
  int[] data;

  public boolean init(int size) {
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