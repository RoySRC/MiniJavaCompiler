

class A {
  public static void main(String[] args) {

  }
}

class B extends C {

}

// TE: the following three classes are in a circular inheritance

class C extends D {

}

class D extends E {

}

class E extends C {

}