

// Test
//   Array in general
class test19 {
  public static void main(String[] a){
    int [] A;
    int [] B;
    int [] C;
    int i;
    int j;
    int N;
    int tmp1;
    int tmp2;

    {
      N = 10;
      A = new int [N];
      tmp1 = N * N;
      B = new int [tmp1];

      i = 0;
      tmp2 = A.length;
      while (i < tmp2) {
        A[i] = i;

        j = 0;
        while (j < N) {
          tmp1 = i*N;
          tmp1 = tmp1 + j;
          B[tmp1] = tmp1;
          j = j + 1;
        }

        i = i + 1;
      }

      i = 0;
      tmp2 = this.length; // TE
      while (i < tmp2) {
        System.out.println(A[i]);
        tmp1 = i*N;
        System.out.println(B[tmp1]);   // If you get parsing error for this line, it might be
        // a bug in minijava.jj for ArrayLookup(). :P
        // Make sure you change it to
        //   ArrayLookup ::= PrimaryExpression [ Expression() ]
        // instead of
        //   ArrayLookup ::= PrimaryExpression() [ PrimaryExpression() ]
        i = i + 1;
      }

    }
  }
}

