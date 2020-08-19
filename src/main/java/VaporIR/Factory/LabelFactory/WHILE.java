package VaporIR.Factory.LabelFactory;

public class WHILE implements Label {

  private final int counter;

  public WHILE(int counter) {
    this.counter = counter;
  }

  public String getwhileTest() {
    return "while"+this.counter+"_top";
  }

  public String getwhileEnd() {
    return "while"+this.counter+"_end";
  }

}
