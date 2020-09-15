package VaporMIR.Storage.Stack;

import VaporMIR.Storage.Storage;

public class Local extends StackStorage {
  public int index;

  public Local(int idx) {
    index = idx;
  }

  public String toString() {
    return "local["+index+"]";
  }
}
