class test66{
    public static void main(String[] a){
        System.out.println(new Test().start());
    }
}


class Test{
    int a;
    Test test;

    public Test next() {


        Test2 test2;


        test2 = new Test2();

        a = 42;

        return test2;
    }

    public int start(){
        int ret;
        a = 0;
        test = this.next();
        ret = test.geta();
        ret = 42;
        return ret;
    }

    public int geta() { return 0; }

}


class Test2 extends Test{

}