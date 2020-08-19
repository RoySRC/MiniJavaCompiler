class test14 {
    public static void main(String[] a) {
        A a;
        a = new A();

        System.out.println((a.getB()).setValue(12)); // 12
        System.out.println(a.getU()); // 90
        System.out.println((a.getB()).getValue()); // 12

        System.out.println((a.getC()).setValue(24)); // 24
        System.out.println(a.getU()); // 900
        System.out.println((a.getC()).getValue()); // 24

        System.out.println(a.getValue()); // 12

        System.out.println((a.getC()).setValue((a.getB()).getValue())); // 12
    }
}

class A extends B {
    int u;
    public B getB() {
        u = 90;
        return this;
    }
    public int getU() {
        return u;
    }
    public C getC() {
        u = 900;
        return this;
    }
}

class B extends C {
    int value;
    public int setValue(int v) {
        value = v;
        return value;
    }
    public int getValue() {
        return value;
    }
}

class C {
    int value;
    int value2;
    int value3;
    public int setValue(int v) {
        value = v;
        return value;
    }
    public int getValue() {
        return value;
    }
}