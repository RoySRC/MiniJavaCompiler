package VaporIR.Factory.LabelFactory;

public class NULL implements Label {

  private final int counter;

  public NULL(int counter) {
    this.counter = counter;
  }

  public String getNullLabel() {
    return "null"+this.counter;
  }

}
