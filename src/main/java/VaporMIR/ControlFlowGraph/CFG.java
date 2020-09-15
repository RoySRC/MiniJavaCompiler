package VaporMIR.ControlFlowGraph;

import VaporMIR.RegisterAllocation.VaporVariable;
import cs132.vapor.ast.*;
import core.util.LOGGER;
import core.util.Interval;
import java.util.*;

import static core.util.util.isNumeric;

/**
 * This class is for building the control flow graph
 */

public class CFG extends VInstr.Visitor<Throwable> {
  // for logging
  private static final transient LOGGER log = new LOGGER(CFG.class.getSimpleName(), true);

  /**
   * The size of the out stack, the value of this is 4 less than the function call with the largest number of arguments
   */
  public int outStackSize = 0;

  /**
   * The root node of the CFG
   */
  public CFGNode rootNode;
  /**
   * The mapping between a vapor code line and its abstract syntax tree node
   */
  private TreeSet<CFGNode> lineASTMap = new TreeSet<>(((o1, o2) -> o1.vmNode.sourcePos.line-o2.vmNode.sourcePos.line));
  /**
   * Store a map between each variable used in the vapor function and its interval of live ranges
   */
  public TreeSet<VaporVariable> interval = new TreeSet<>((o1, o2) -> o1.name.compareTo(o2.name));
  /**
   * Pointer to the current node int the CFG when building the edges. This is only used when building the edges.
   */
  private CFGNode currentNode;

  public ArrayList<String> args = new ArrayList<>();

  /**
   * Constructor
   * @param function the vapor function
   * @throws Throwable throws an error if the vapor code is incorrect
   */
  public CFG(VFunction function) throws Throwable {
    log.info("Working with function: "+function.ident);
    rootNode = new CFGNode(function, 0);
    currentNode = rootNode;
    generateNodes(function);

    // Generate the edges
    generateEdges(function);

    // generate the live in and live out sets for all nodes in the CFG
    generateLiveInOut();

    // generate the intervals
    generateLiveIntervals();

    generatePersistance();

    if (log.getLogStatus()) {
      print();
      System.out.printf("%3s | %-23s | %-23s | %-33s | %-33s\n", "ID", "Use Set", "Def Set", "Live In", "Live Out");
      System.out.print("--------------------------------------------------------------------------------------");
      System.out.print("-------------------------------------");
      System.out.println();
      for (CFGNode node : lineASTMap) {
        node.print();
      }
    }
  }

  /**
   * Iterate through the function and add all the nodes of basic block 1 to the list of nodes. Also add the adt node
   * object and it line number to the @lineASTMap
   * @param function the function object
   */
  private void generateNodes(VFunction function) {
    // insert the root node
    lineASTMap.add(rootNode);

    int id = 1;

    // insert the function params
    for (VVarRef.Local arg : function.params) {
      CFGNode node = new CFGNode(arg, id++);
      node.defSet.add(arg.toString());
      lineASTMap.add(node);
      args.add(arg.ident);
    }

    // Add all the labels in the function to the CFG first. This is because branch statements can point to any
    // labels, either before or after the branch declaration
    for (VCodeLabel codeLabel : function.labels) {
      lineASTMap.add(new CFGNode(codeLabel, id++));
    }

    // Finally add all of the remaining instructions to the list of nodes
    for (VInstr instr : function.body) {
      lineASTMap.add(new CFGNode(instr, id++));
    }
  }

  /**
   * Given a function and a set of nodes, connect the nodes to form edges in the CFG
   * @param function The vapor function
   * @throws Throwable throws an error if the vapor code is incorrect
   */
  private void generateEdges(VFunction function) throws Throwable {
    for (CFGNode n : lineASTMap) {
      if (n.vmNode instanceof VVarRef.Local) {
        n.parents.add(currentNode);
        currentNode.children.add(n);
        currentNode = n;
      }
    }
    for (VInstr instr : function.body) {
      log.info("Current line: " + instr.sourcePos.line);
      instr.accept(this);
    }
  }

  /**
   * Perform the persistance analysis. This is to backup variables to the stack in between function calls.
   */
  private void generatePersistance() {
    ArrayList<VaporVariable> varsBeforeCall = new ArrayList<>();
    for (Iterator<CFGNode> n=lineASTMap.iterator(); n.hasNext();) {
      CFGNode node = n.next();
      if (!(node.vmNode instanceof VCall)) continue;
      final int cl = node.vmNode.sourcePos.line;
      // variables before cl
      for (VaporVariable v : interval) {
        log.info(v+": line: "+v.line);
        if (v.line < cl) {
          varsBeforeCall.add(v);
        }
      }
      // check if variables before cl are used after cl
      for (VaporVariable v : varsBeforeCall) {
        for (CFGNode x : lineASTMap) {
          if (x.vmNode.sourcePos.line > cl) { // this will overestimte
            if (x.useSet.contains(v.name)) v.persistent = true;
          }
        }
      }
      varsBeforeCall.clear();
    }
    if (log.getLogStatus()) {
      log.info("liveness intervals: ");
      for (VaporVariable V : interval) {
        log.info("\t"+V.name+" -> "+V.interval);
      }
      log.info("Persistence Information:");
      for (VaporVariable V : interval) {
        log.info("\t"+V.name+" -> "+V.persistent);
      }
    }
  }

  private void generateLiveInOut() {
    for (CFGNode node : lineASTMap) {
      for (String var :node.useSet) {
        node.liveIn.add(var);
      }
    }

    for (CFGNode node : lineASTMap) {
      for (String var : node.liveIn) {
        for (CFGNode parent : node.parents) {
          parent.liveOut.add(var);
        }
      }
    }

    for (CFGNode node : lineASTMap) {
      for (String var : node.liveOut) {
        if (!node.defSet.contains(var)) {
          node.liveIn.add(var);
        }
      }
    }
  }

  /**
   * Generate the set of live intervals for all variables in the vapor program
   */
  private void generateLiveIntervals() {
    for (VaporVariable var : interval) {
      for (CFGNode node : lineASTMap) {
          var.interval.add(node.vmNode.sourcePos.line);
      }
    }
  }

  /**
   * From the arraylist of @nodes get the @CFGNode whose @vmNode is the one provided in the argument
   * @param vmNode the node to search for
   * @return the CFG node that has the vmNode
   */
  private CFGNode getCFGNode(Node vmNode) {
    for (CFGNode node : lineASTMap) {
      if (node.vmNode == vmNode)
        return node;
    }
    return null;
  }

  /**
   * The following function can only be used for finding the CFGNodes for labels, this is because {@link VAddr}
   * cannot be cast to {@link VCodeLabel}.
   * @param label the code label to search for
   * @return the CFG node encapsulating the label
   */
  private CFGNode getCFGNode(final String label) {
    for (CFGNode node : lineASTMap) {
      if (node.vmNode instanceof VCodeLabel) {
        String codeLabel = ":"+((VCodeLabel) node.vmNode).ident;
        if (codeLabel.equals(label))
          return node;
      }
    }
    return null;
  }

  /**
   * Print the cfg as an adjacency list
   */
  public void print() {
    for (CFGNode node : lineASTMap) {
      System.out.print(node.vmNode.sourcePos.line+" ");
      node.printChildren();
    }
  }

  /**
   * Methods inherited from visitor
   */

  @Override
  public void visit(VAssign vAssign) {
    log.info("Entered VAssign. line: "+vAssign.sourcePos+", obj: "+vAssign.toString());

    CFGNode node = getCFGNode(vAssign); // get the CFG node associated with vAssign
    assert node != null;
    log.info("Creating edge between line "+currentNode.vmNode.sourcePos.line+" and "+node.vmNode.sourcePos.line);
    currentNode.children.add(node);
    node.parents.add(currentNode);
    currentNode = node;
    log.info("Current node changed to: "+currentNode.vmNode.sourcePos.line);

    // fill the use-def sets
    node.defSet.add(vAssign.dest.toString());
    interval.add(new VaporVariable(vAssign.dest.toString(), new Interval(vAssign.dest.toString()), vAssign.sourcePos.line));
    if (!isNumeric(vAssign.source.toString())) {
      // We do not need to use any registers if the rhs is a number
      node.useSet.add(vAssign.source.toString());
      if (vAssign.source instanceof VVarRef.Local) {
        log.info("rhs of vassign is a function parameter.");
        log.info("insert "+vAssign.source);
        interval.add(new VaporVariable(vAssign.source.toString(), new Interval(vAssign.source.toString()),
            vAssign.sourcePos.line));
      }
    }

    log.info("Left VAssign.  line: "+vAssign.sourcePos+", obj: "+vAssign.toString());
  }

  @Override
  public void visit(VCall vCall) {
    log.info("Entered VCall. line: "+vCall.sourcePos+", obj: "+vCall.toString());

    if (vCall.args.length-4>0 && vCall.args.length-4>outStackSize)
      outStackSize = vCall.args.length-4;

    CFGNode node = getCFGNode(vCall);
    assert node != null;
    log.info("Creating edge between line "+currentNode.vmNode.sourcePos.line+" and "+node.vmNode.sourcePos.line);
    currentNode.children.add(node);
    node.parents.add(currentNode);
    currentNode = node;
    log.info("Current node changed to: "+currentNode.vmNode.sourcePos.line);

    // fill the use-def sets
    if (vCall.dest != null) {
      node.defSet.add(vCall.dest.toString());
      interval.add(new VaporVariable(vCall.dest.toString(), new Interval(vCall.dest.toString()), vCall.sourcePos.line));
    }
    for (VOperand operand : vCall.args) { // all the arguments are being used
      if (!isNumeric(operand.toString())) {
        node.useSet.add(operand.toString());
        // this is for the case when someone passes a function argument to a call statement
        if (operand instanceof VVarRef.Local) {
          log.info("argument of vcall is a function parameter.");
          log.info("insert "+operand.toString());
          interval.add(new VaporVariable(operand.toString(), new Interval(operand.toString()), vCall.sourcePos.line));
        }
      }
    }
    node.useSet.add(vCall.addr.toString());

    log.info("Left VCall. line: "+vCall.sourcePos+", obj: "+vCall.toString());
  }

  @Override
  public void visit(VBuiltIn vBuiltIn) {
    log.info("Entered VBuiltIn. line: "+vBuiltIn.sourcePos+", obj: "+vBuiltIn.toString());

    CFGNode node = getCFGNode(vBuiltIn);
    assert node != null;
    log.info("Creating edge between line "+currentNode.vmNode.sourcePos.line+" and "+node.vmNode.sourcePos.line);
    currentNode.children.add(node);
    node.parents.add(currentNode);
    if (vBuiltIn.op != VBuiltIn.Op.Error) {
      currentNode = node;
    }
    log.info("Current node changed to: "+currentNode.vmNode.sourcePos.line);

    // build the use def sets
    if (vBuiltIn.op != VBuiltIn.Op.Error) {
      for (VOperand operand : vBuiltIn.args) { // all the operands are being used
        if (!isNumeric(operand.toString())) {
          node.useSet.add(operand.toString());
          // this is for the case when someone passes a function argument to a builtin such as in the MoreThan4 example
          if (operand instanceof VVarRef.Local) {
            log.info("argument of vBuiltin is a function parameter.");
            log.info("insert "+operand.toString()+" into interval.");
            interval.add(new VaporVariable(operand.toString(), new Interval(operand.toString()), vBuiltIn.sourcePos.line));
          }
        }
      }
      if (vBuiltIn.dest != null) {
        node.defSet.add(vBuiltIn.dest.toString());
        interval.add(new VaporVariable(vBuiltIn.dest.toString(), new Interval(vBuiltIn.dest.toString()),
            vBuiltIn.sourcePos.line));
      }
    } else log.info("VBuiltIn is an Error statement, ignoring.");

    log.info("Left VBuiltIn. line: "+vBuiltIn.sourcePos+", obj: "+vBuiltIn.toString());
  }

  @Override
  public void visit(VMemWrite vMemWrite) {
    log.info("Entered VMemWrite. line: "+vMemWrite.sourcePos+", obj: "+vMemWrite.toString());

    CFGNode node = getCFGNode(vMemWrite);
    assert node != null;
    log.info("Creating edge between line "+currentNode.vmNode.sourcePos.line+" and "+node.vmNode.sourcePos.line);
    currentNode.children.add(node);
    node.parents.add(currentNode);
    currentNode = node;
    log.info("Current node changed to: "+currentNode.vmNode.sourcePos.line);

    // Instructions will always be of the form: [a+4] = v
    // line will always begin with [a+12], which can be decomposed into base - a, and offset - 12. dest does not have
    // an overridden toString() method, therefore cast to VMemRef.Global since we are always accessing the heap, and
    // use the overridden toString() method of the base member.
    String destination = ((VMemRef.Global)vMemWrite.dest).base.toString();
    node.useSet.add(destination);
    interval.add(new VaporVariable(destination, new Interval(destination), vMemWrite.sourcePos.line));
    if (!isNumeric(vMemWrite.source.toString())) {
      node.useSet.add(vMemWrite.source.toString());
      // this is for the case when the rhs is a function parameter
      if (vMemWrite.source instanceof VVarRef.Local) {
        log.info("lhs of vMemWrite is a function parameter.");
        log.info("insert "+vMemWrite.source.toString());
        interval.add(new VaporVariable(vMemWrite.source.toString(), new Interval(vMemWrite.source.toString()), vMemWrite.sourcePos.line));
      }
    }

    log.info("Left VMemWrite. line: "+vMemWrite.sourcePos+", obj: "+vMemWrite.toString());
  }

  @Override
  public void visit(VMemRead vMemRead) {
    log.info("Entered VMemRead. line: "+vMemRead.sourcePos+", obj: "+vMemRead.toString());

    CFGNode node = getCFGNode(vMemRead);
    assert node != null;
    log.info("Creating edge between line "+currentNode.vmNode.sourcePos.line+" and "+node.vmNode.sourcePos.line);
    currentNode.children.add(node);
    node.parents.add(currentNode);
    currentNode = node;
    log.info("Current node changed to: "+currentNode.vmNode.sourcePos.line);

    // Instructions will always be of the form: v = [a+4]
    node.defSet.add(vMemRead.dest.toString());
    interval.add(new VaporVariable(vMemRead.dest.toString(), new Interval(vMemRead.dest.toString()),
        vMemRead.sourcePos.line));
    // we are doing this for the same reason in VMemWrite
    node.useSet.add(((VMemRef.Global)vMemRead.source).base.toString());
    // this is for the case when the destination is a function argument
    if (vMemRead.dest instanceof VVarRef.Local) {
      log.info("lhs of vMemRead is a function parameter.");
      String baseIdentifier = ((VMemRef.Global) vMemRead.source).base.toString();
      log.info("insert "+baseIdentifier);
      interval.add(new VaporVariable(baseIdentifier, new Interval(baseIdentifier), vMemRead.sourcePos.line));
    }

    log.info("Left VMemRead. line: "+vMemRead.sourcePos+", obj: "+vMemRead.toString());
  }

  @Override
  public void visit(VBranch vBranch) {
    log.info("Entered VBranch. line: "+vBranch.sourcePos+", obj: "+vBranch.toString());

    // we need to handle branches differently, since depending on the evaluated conditional we can point to any code
    // segment

    // First handle the conditional node
    CFGNode node = getCFGNode(vBranch);
    assert node != null;
    log.info("Creating edge between line "+currentNode.vmNode.sourcePos.line+" and "+node.vmNode.sourcePos.line);
    currentNode.children.add(node);
    node.parents.add(currentNode);
    currentNode = node;
    log.info("Current node changed to: "+currentNode.vmNode.sourcePos.line);

    // we only care about the conditional being branched on.
    node.useSet.add(vBranch.value.toString());

    // Then handle the branch node
    CFGNode branchNode = getCFGNode(vBranch.target.toString()); // get the CFGNode associated with the branch label
    assert branchNode != null;
    int branchLine = branchNode.vmNode.sourcePos.line;
    CFGNode nextInstruction = null; // the CFGNode pointing to the next instruction
    // get the next instruction after the label that is not a label
    for (CFGNode n : lineASTMap) {
      int line = n.vmNode.sourcePos.line;
      if (line > branchLine && !(n.vmNode instanceof VCodeLabel)) {
        log.info("Found children at line: "+line);
        nextInstruction = n;
        break;
      }
    }
    assert nextInstruction != null;
    nextInstruction.parents.add(node);
    currentNode.children.add(nextInstruction);
    log.info("Creating edge between line "+currentNode.vmNode.sourcePos.line+" and "+node.vmNode.sourcePos.line);

    log.info("Left VBranch. line: "+vBranch.sourcePos+", obj: "+vBranch.toString());
  }

  @Override
  public void visit(VGoto vGoto) {
    log.info("Entered VGoto. line: "+vGoto.sourcePos+", obj: "+vGoto.toString());

    CFGNode node = getCFGNode(vGoto);
    assert node != null;
    log.info("Creating edge between line "+currentNode.vmNode.sourcePos.line+" and "+node.vmNode.sourcePos.line);
    currentNode.children.add(node);
    node.parents.add(currentNode);
    currentNode = node;
    log.info("Current node changed to: "+currentNode.vmNode.sourcePos.line);

    CFGNode branchNode = getCFGNode(vGoto.target.toString());
    assert branchNode != null;
    int gotoLine = branchNode.vmNode.sourcePos.line;
    log.info("Label line: "+gotoLine);
    CFGNode label = null;
    for (CFGNode n : lineASTMap) { // get the next instruction after the label that is not a label
      int line = n.vmNode.sourcePos.line;
      if (line > gotoLine && !(n.vmNode instanceof VCodeLabel)) {
        log.info("Found children at line: "+line);
        label = n;
        break;
      }
    }
    assert label != null;
    label.parents.add(node);
    node.children.add(label);
    // set currentNode to the next instruction after the goto statement that is not a label
    for (CFGNode n : lineASTMap) {
      if (n.vmNode.sourcePos.line > node.vmNode.sourcePos.line && !(n.vmNode instanceof VCodeLabel)) {
        currentNode = n;
        break;
      }
    }
    log.info("Current node changed to: "+currentNode.vmNode.sourcePos.line);

    log.info("Left VGoto."+vGoto.sourcePos+", obj: "+vGoto.toString());
  }

  @Override
  public void visit(VReturn vReturn) {
    log.info("Entered VReturn. line: "+vReturn.sourcePos+", obj: "+vReturn.toString());

    CFGNode node = getCFGNode(vReturn);
    assert node != null;
    log.info("Creating edge between line "+currentNode.vmNode.sourcePos.line+" and "+node.vmNode.sourcePos.line);
    // prevent a node from pointing to itself
    if (node.vmNode.sourcePos.line != currentNode.vmNode.sourcePos.line) {
      currentNode.children.add(node);
      node.parents.add(currentNode);
    }
    currentNode = node;
    log.info("Current node changed to: "+currentNode.vmNode.sourcePos.line);

    // Instructions are of the form: ret a. Here, 'a' is being used, so put this in the useSet
    if (vReturn.value != null)
      if (!isNumeric(vReturn.value.toString()))
        node.useSet.add(vReturn.value.toString());

    log.info("Left VReturn. line: "+vReturn.sourcePos+", obj: "+vReturn.toString());
  }
}
