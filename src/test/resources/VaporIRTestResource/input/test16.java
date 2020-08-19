class test16 {
    public static void main(String[] args) {
        A a;
        a = new A();
        System.out.println((a.getB()).getValue()); //99
    }
}

class A {
    public B getB() {
        B b;
        b = new B();
        return b;
    }
}

class B {
    int value;
    public int getValue() {
        value = 99;
        return value;
    }
}