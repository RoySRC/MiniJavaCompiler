package VaporIR.Factory.LabelFactory;

public class IF_ELSE implements Label {

  private final int counter;

  public IF_ELSE(int counter) {
    this.counter = counter;
  }

  public String getIfElse() {
    return "if"+this.counter+"_else";
  }

  public String getIfEnd() {
    return "if"+this.counter+"_end";
  }

}
