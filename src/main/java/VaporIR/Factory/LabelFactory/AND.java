package VaporIR.Factory.LabelFactory;

public class AND implements Label {

  private int counter = 0;

  public AND(int counter) {this.counter = counter;}

  public String getElse() {
    return "ss"+counter+"_else";
  }

  public String getEnd() {
    return "ss"+counter+"_end";
  }

}
