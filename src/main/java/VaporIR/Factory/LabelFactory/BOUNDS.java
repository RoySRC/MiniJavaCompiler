package VaporIR.Factory.LabelFactory;

public class BOUNDS implements Label {

  private int counter;

  public BOUNDS(int counter) {
    this.counter = counter;
  }

  public String getBoundsLabel() {
    return "bounds"+this.counter;
  }

}
