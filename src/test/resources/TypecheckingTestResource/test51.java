

// Test
//   Class and method
class test25 {
    public static void main(String[] str_){
      Foo f;
      Foo cpy;
      int a;
      int b;
      {
        f = new Foo();
        cpy = f.m1();
        a = cpy.m2();
        System.out.println(a);
      }
    }
}

class Foo {
  public Foo m1() {
    return new Foo();
  }

  public int m2() {
    System.out.println(456);
    return 1;
  }

}

