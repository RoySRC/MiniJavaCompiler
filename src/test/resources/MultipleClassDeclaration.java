class Scope {
  public static void main(String[] a) {
    int a;
    int a;
  }
}

class __Scope {
  public int x() {
    return 2;
  }
}

class _Scope extends __Scope {

  public int x() {
    int a;
    {
      a = 1;
    }
    a = false;
    return a;
  }

}

class _Scope {
  public int y() {
    return 1;
  }
}