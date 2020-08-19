

/**
 * The folowing typechecks
 */

class test78 {
  public static void main(String[] args) {
    Vector v;
    v = new Vector();

    System.out.println(v.print());
    System.out.println(v.push_back(12));
    System.out.println(v.print());
  }
}

class Vector {
  int[] data;

  public int init() {
    data = new int[0];
    return data.length;
  }

  public int print() {
    int size;
    int i;

    size = data.length;
    i = 0;

    while (i < (data.length)) {
      System.out.println(data[i]);
      i = i + 1;
    }

    return i;
  }

  public int push_back(int i) {
    int[] temp;
    int minRequiredSize;
    int index;

    index = 0;
    temp = data;
    minRequiredSize = (data.length) + 1;

    if ((data.length) < minRequiredSize) {
      data = new int[minRequiredSize];
      while (index < (temp.length)) {
        data[index] = temp[index];
        index = index + 1;
      }
      data[index] = i;
    } else {}

    return data.length;
  }

  public int get(int index) {
    return data[index];
  }

  public int size() {
    return (data.length);
  }
}