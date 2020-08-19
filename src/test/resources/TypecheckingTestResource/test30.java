

class p_Dog {

  public static void main (String [] id) {
    System.out.println(new Cat().woof(false));
  }

}


class Cat {
  int lmao;

  public int woof(boolean x){
    boolean local;
    int i;
    int rv;
    local = x;
    if(x){
      rv = 0;
    }
    else {
      i = new Fish().blub(true);
    }
    return rv;
  }

}

class Fish {
  Cat c;

  public int blub(boolean y){

    int x;
    c = new Cat();
    x = c.woof(y);
    return 0;
  }

}
