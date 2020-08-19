package MIPS;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public class MIPSProgram {

  public LinkedList<String> instructions = new LinkedList<>();
  private String indent = new String();

  public MIPSProgram() {}

  public void indent() {
    indent += "  ";
  }

  public void outdent() {
    indent = indent.substring(0, indent.length()-2);
  }

  public void add(String instr) {
    int callingLine = Thread.currentThread().getStackTrace()[2].getLineNumber();
    instructions.add(indent+instr);
  }

  public void addNewLine() {
    instructions.add("");
  }

  public void addAll(Collection<String> c) {
    for (String e : c) {
      add(e);
    }
  }

  public int size() {
    return instructions.size();
  }

  /**
   * Print the current vaporM program to stdout
   */
  public void print() {
    for (String instr : instructions) {
      System.out.println(instr);
    }
  }

  public byte[] getByteArray() {
    StringBuilder sb = new StringBuilder();
    for (String instr : instructions) {
      sb.append(instr);
      sb.append("\n");
    }
    return sb.toString().getBytes();
  }

}
