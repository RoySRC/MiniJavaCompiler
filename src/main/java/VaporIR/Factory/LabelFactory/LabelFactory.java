package VaporIR.Factory.LabelFactory;

public class LabelFactory {

  private int NULL_COUNTER = 1;
  private int WHILE_COUNTER = 1;
  private int BOUNDS_COUNTER = 1;
  private int AND_COUNTER = 1;
  private int IF_COUNTER = 1;

  private static final LabelFactory instance = new LabelFactory();

  private LabelFactory() {}

  public static enum TYPE {
    IF_ELSE, WHILE, NULL, BOUNDS, TMPVAR, AND
  }

  public static LabelFactory getInstance() {return instance;}

  public Label createLabel(TYPE type) {
    switch (type) {
      case IF_ELSE:
        return new IF_ELSE(IF_COUNTER++);

      case WHILE:
        return new WHILE(WHILE_COUNTER++);

      case NULL:
        return new NULL(NULL_COUNTER++);

      case BOUNDS:
        return new BOUNDS(BOUNDS_COUNTER++);

      case TMPVAR:
        return new TMPVAR();

      case AND:
        return new AND(AND_COUNTER++);

      default:
    }
    return null;
  }

}
