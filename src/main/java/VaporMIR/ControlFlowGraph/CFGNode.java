package VaporMIR.ControlFlowGraph;

import cs132.vapor.ast.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CFGNode {

  public int ID = -1;
  public Node vmNode;
  public Set<CFGNode> parents = new HashSet<>();
  public Set<CFGNode> children = new HashSet<>();

  // use-def set
  public Set<String> useSet = new HashSet<>();
  public Set<String> defSet = new HashSet<>();

  // live-in and live-out set
  public Set<String> liveIn = new HashSet<>();
  public Set<String> liveOut = new HashSet<>();

  public CFGNode(Node vmNode, int id) {
    this.vmNode = vmNode;
    ID = id;
  }

  public void printChildren() {
    for (CFGNode N : children)
      System.out.print(N.vmNode.sourcePos.line+" ");
    System.out.println();
  }

  public void print() {
    System.out.printf("%-3d | %-23s | %-23s | %-33s | %-33s\n",
        vmNode.sourcePos.line, useSet, defSet, liveIn, liveOut);
  }

}
