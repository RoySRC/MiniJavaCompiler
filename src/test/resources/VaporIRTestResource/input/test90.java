class test90{
  public static void main(String[] a){
    System.out.println(new Test().start());
  }
}

class Test {

  Test test;
  int[] i;

  public int start(){
    Test foo;
    foo = this;
    i = new int[10];
    test = foo.next(i);

    return i[3];
  }

  public Test next(int[] j){
    j[3] = 42;
    return test;
  }
}