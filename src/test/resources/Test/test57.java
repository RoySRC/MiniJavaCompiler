

// Test
//   Class and method
class test22 {
  public static void main(String[] str_){
    Foo f;
    int a;
    int b;
    {
      f = new Foo();
      f = f.m1(); // TE
      b = f.m2();

      System.out.println(a);
      System.out.println(b);
    }
  }
}

class Foo {
  public int m1() {
    int a;
    System.out.println(123);
    a = this.m2();
    return a;
  }

  public int m2() {
    System.out.println(456);
    return 1;
  }

}

