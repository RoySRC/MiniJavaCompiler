

/**
 * The following class type checks
 */

class p_Dog {

  public static void main (String [] id) {
    int x;
    {
      System.out.println(new cat().newMethod(5));
      x = 10 + 20;
      x = x - 4;
      System.out.println(x);
      System.out.println(305);
      x = 2 + 2;
      System.out.println(x);
      x = 5 + 3;
      x = x * 2;
      x = x - 3;
      x = x * 5;
      x = x + 4;
      x = x - 23;
      x = x + 2;
      x = x - 9;
      x = x * 1;
      x = x - 3;
      x = x + 5;
      x = x + 4;
      x = x+ 3;
      x = x - 4;
      x = x- 5;
      x = x * 3;
      System.out.println(x);
    }

  }

}

class cat {

  public int newMethod(int y){
    int x;
    x = y;
    System.out.println(x);
    return x;
  }
}