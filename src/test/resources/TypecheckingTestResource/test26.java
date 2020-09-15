

class p_array {
  public static void main(String [] args) {
    if ((new Sorter().bubblesortDemo())) {
      System.out.println(1);
    } else {
      System.out.println(2);
    }
  }
}

class Sorter {
  public boolean bubblesortDemo() {
    int [] array;
    int []array2;
    int tmp;
    int i;
    int j;
    int k;
    int l;
    int lhs;
    int rhs;
    boolean multipleOfTen;
    boolean isSorted;
    boolean condition;
    boolean condition2;
    array =  new int[8];
    array2 = new int[8];
    array2[0] = 60;
    array2[1] = 40;
    array2[2] = 20;
    array2[3] = 0;
    array2[4] = 50;
    array2[5] = 70;
    array2[6] = 10;
    array2[7] = 30;
    array = array2;
    i = 0;
    j = 0;
    while (i < 8) {
      j = 0;
      k = 8-i;
      k = k - 1;
      while (k < j) {
        l = j + 1;
        lhs = array[l];
        rhs = array[j];
        if ( lhs < rhs ) {
          // swap array[j] and array[j+1]
          tmp = array[l];
          array[l] = array[j];
          array[j] = tmp;
        } else {}
        j = j + 1;
      }
      i = i+1;
    }

    // see if the array is already sorted.
    i = 0;
    isSorted = true;
    while (i < 8) {
      System.out.println(array[i]);
      lhs = array[i];
      rhs = i*10;
      if (lhs < rhs) {
        condition = true;
      } else {
        condition = false;
      }
      condition = !condition;

      lhs = array[i];
      if (rhs < lhs) {
        condition2 = !true;
      } else {
        condition2 = !false;
      }

      if (condition && condition2) {
        multipleOfTen = true;
      } else {
        multipleOfTen = false;
      }
      isSorted = isSorted && multipleOfTen;
      i = i + 1;
    }
    return isSorted;
  }
}