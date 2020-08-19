

/**
 * The following class typechecks
 */

class p_mainClass {

  public static void main(String[] args){
    System.out.println(new fakeMain().run());
  }
}

class fakeMain {
  public int run(){
    Vehicle a;
    Vehicle b;
    int ret;
    a = new Vehicle();
    b = new Vehicle();

    ret = a.create(5, 2);
    ret = b.create(2, 10);

    ret = a.move();
    ret = b.setOn(true);
    ret = b.move();
    ret = b.setOn(false);
    ret = b.move();
    ret = a.setOn(true);
    ret = a.move();
    ret = b.move();
    return 0;

  }

}

class Vehicle {
  int moveSpeed;
  int capacity;
  boolean on;
  public int create(int speed, int xCapacity){
    moveSpeed = speed;
    capacity = xCapacity;
    on = false;
    return 0;
  }
  public int getSpeed(){
    return moveSpeed;
  }

  public int setSpeed(int a){
    moveSpeed = a;
    return 0;
  }

  public int setOn(boolean a){
    on = a;
    return 0;
  }

  public int move(){
    if(on){
      System.out.println(12);
      System.out.println(moveSpeed);
      System.out.println(13);
      System.out.println(14);
      System.out.println(capacity);
      System.out.println(55);

    }else{
      System.out.println(99999999999999);
    }
    return moveSpeed;
  }


}