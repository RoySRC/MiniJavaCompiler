

// Test
//   Array in general
class test20 {
    public static void main(String[] a){
     int [] A;
     int i;
     int tmp1;
     int tmp2;
     {
       A = new int [10];

       i = 10;
       while (0 < i) {
         i = i - 1;
         A[i] = i+1;
       }

       tmp1 = A[0];
       tmp1 = A[ tmp1 ];
       A[9] = A[ tmp1 ];
       tmp2 = A[0];
       tmp1 = tmp2 * 30;
       tmp2 = A[8];
       A[8] = tmp2 + tmp1;

       i = 0;
       tmp2 = A.length;
       while (i < tmp2) {
         System.out.println(A[i]);
         i = i + 1;
       }
     }
    }
}

