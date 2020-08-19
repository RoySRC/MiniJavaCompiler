package VaporMIR.Storage.Stack;

import VaporMIR.Storage.Storage;

public class In extends StackStorage {
  public int index;

  public In(int idx) {
    index = idx;
  }

  public String toString() {
    return "in["+index+"]";
  }
}
