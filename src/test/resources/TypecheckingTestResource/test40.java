

class A {
  public static void main(String[] args) {

  }
}

// TE: the following three classes are in a circular inheritance


class B {

}

class C extends D {

}

class D extends E {

}

class E extends B {

}