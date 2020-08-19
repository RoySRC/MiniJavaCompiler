// This should typecheck

class A {
  public static void main(String[] args) {

  }
}

class B extends C {
  int b;
   int b() {
     return b;
   }
}

class C {
  int b;
  int b() {
    return b;
  }
}