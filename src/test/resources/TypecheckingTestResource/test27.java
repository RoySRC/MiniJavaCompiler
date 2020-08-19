

/**
 * The following typechecks
 */

class p_number {
  public static void main(String [] a) {
    {
      System.out.println(new Numbers().choose(10, 4));
    }
  }
}

class Numbers {
  public int choose(int n, int k) {
    int res;
    int i;
    int tmp1;
    if (!(n<k)) {
      // n! / (k! (n-k)!)
      i = 1;
      res = 1;
      tmp1 = k + 1;
      while (i < tmp1) {
        tmp1 = n-k;
        tmp1 = tmp1+i;
        tmp1 = tmp1 * res;

        while (i < tmp1) {
          tmp1 = tmp1 - i;
        }

        res = tmp1;
        i = i + 1;
      }
    } else {
      res = 0;
    }
    return res;
  }

}