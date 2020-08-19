package VaporIR.Factory.LabelFactory;

import core.util.LOGGER;

public class TMPVAR implements Label {
  // Logger
  private transient static final LOGGER log = new LOGGER(TMPVAR.class.getName());

  private int COUNTER = -1;

  public TMPVAR() {}

  public String get() {
    return "t."+COUNTER;
  }

  public String getPrev() {
    return "t."+(COUNTER-1);
  }

  public String getNext() {
    return "t."+(COUNTER+1);
  }

  public void setCOUNTER(int v) {
    COUNTER = v;
  }

  public void createNew() {
    int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
    log.info("created new temporary: "+(COUNTER+1), line);
    COUNTER++;
  }

  public int getCOUNTER() {
    return COUNTER;
  }

  public void reset() {
    COUNTER = -1;
  }

  public void delete() {
    COUNTER--;
  }

}
