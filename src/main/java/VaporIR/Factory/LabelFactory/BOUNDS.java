package VaporIR.Factory.LabelFactory;

public class BOUNDS implements Label {

  private final int counter;

  public BOUNDS(int counter) {
    this.counter = counter;
  }

  public String getBoundsLabel() {
    return "bounds"+this.counter;
  }

}
