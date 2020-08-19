

// Test
//   Passing array via parameter
//   Returning an array from a method
class test27 {
  public static void main(String[] args) {
    Foo foo;
    int [] B;
    int i;

    int tmp1;
    int[] tmpA1;

    {
      foo = new Foo();
      B = new int [10];
      i = 0;
      tmp1 = B.length;
      while (i < tmp1) {
        B[i] = i+1;
        i = i + 1; 
      }

      tmpA1 = foo.bar(B);
      System.out.println(tmpA1[1]);
      System.out.println(tmpA1[4]);
    }
  }
}

class Foo {

  public int [] bar(int [] A) {
    int i;
    int tmp;
    int tmp2;

    i = 0;
    tmp = A.length;
    while (i < tmp) {
      tmp2 = A[i];
      tmp2 = tmp2 * 2;
      A[i] = tmp2;
      i = i + 1;
    }

    return A;
  }
}
