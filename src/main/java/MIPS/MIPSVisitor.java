package MIPS;

import cs132.vapor.ast.*;
import core.util.LOGGER;
import static core.util.util.isNumeric;
import java.util.*;

/**
 * External Resources:
 *  https://user.eng.umd.edu/~manoj/759M/MIPSALM.html
 *  https://www.dsi.unive.it/~gasparetto/materials/MIPS_Instruction_Set.pdf
 */


public class MIPSVisitor extends VInstr.Visitor<Throwable> {
  private static final transient LOGGER log = new LOGGER(MIPSVisitor.class.getSimpleName(), true);

  public MIPSProgram mipsProgram = new MIPSProgram();
  public int inStackSize = 0;
  public int outStackSize = 0;
  public int localStackSize = 0;
  public int frameSize;
  public Set<String> reservedRegister = new HashSet<String>(){{ add("$t9"); add("$v1"); }};
  public int currentLocalFramePointer = 0;
  public int currentOutFramePointer = 0;

  public MIPSVisitor(VaporProgram program) throws Throwable {
    mipsProgram.add(".data");
    mipsProgram.addNewLine();

    // Copy the function data segment
    for (VDataSegment d : program.dataSegments) {
      mipsProgram.add(d.ident+":");
      mipsProgram.indent();
      for (VOperand.Static values : d.values) {
        mipsProgram.add(values.toString().substring(1));
      }
      mipsProgram.outdent();
      mipsProgram.addNewLine();
    }

    mipsProgram.addAll(Prologue());
    mipsProgram.addNewLine();


    for (VFunction function : program.functions) {
      log.info(log.PURPLE("Working with function: "+function.ident));
      inStackSize = function.stack.in;
      outStackSize = function.stack.out;
      localStackSize = function.stack.local;
      frameSize = (4*(2+localStackSize+outStackSize));
      currentLocalFramePointer = 2;
      currentOutFramePointer = localStackSize;
      log.info(String.format("(in, out, local) = (%d, %d, %d)",inStackSize, outStackSize, localStackSize));
      log.info("frameSize: "+frameSize);

      mipsProgram.add(function.ident+":");
      mipsProgram.indent();
      mipsProgram.addAll(FunctionPrologue());


      final Map<Integer, String> lineLabelMap = new HashMap<Integer, String>(){{
        for (VCodeLabel lbl : function.labels) put(lbl.sourcePos.line, lbl.ident+":");
      }};

      int previousLine = function.sourcePos.line;
      for (VInstr instr : function.body) {
        final int currentLine = instr.sourcePos.line;
        for (int i=previousLine; i<currentLine; ++i) {
          if (lineLabelMap.containsKey(i)) {
            mipsProgram.outdent();
            mipsProgram.add(lineLabelMap.get(i));
            mipsProgram.indent();
          }
          previousLine = currentLine;
        }

        instr.accept(this);
      }


      mipsProgram.addAll(FunctionEpilogue());
      mipsProgram.outdent();
      mipsProgram.addNewLine();
    }

    mipsProgram.addAll(PrintIntS());
    mipsProgram.addNewLine();
    mipsProgram.addAll(Error());
    mipsProgram.addNewLine();
    mipsProgram.addAll(HeapAlloc());
    mipsProgram.addNewLine();
    mipsProgram.addAll(Epilogue());
  }

  /**
   * These are: $a0 = $t0
   *          | $a1 = 10
   *          | $a0 = :label
   */
  @Override
  public void visit(VAssign vAssign) throws Throwable {
    log.info("Entered vAssign: "+vAssign.sourcePos.line);
    String srcString = vAssign.source.toString();
    String dstString = vAssign.dest.toString();

    if (isNumeric(srcString)) {
      mipsProgram.add("li "+dstString+" "+srcString);

    } else if (srcString.charAt(0)==':') { // src is an address
      mipsProgram.add("la "+dstString+" "+srcString.substring(1));

    } else { // source is a register
      mipsProgram.add("move "+dstString+" "+srcString);
    }

    log.info("Left vAssign: "+vAssign.sourcePos.line);
  }

  /**
   * These are: call t.0
   *          | call :label
   */
  @Override
  public void visit(VCall vCall) throws Throwable {
    log.info("Entered vCall: "+vCall.sourcePos.line);
    String addressString = vCall.addr.toString();
    mipsProgram.add(
        (addressString.charAt(0)==':'?"jal "+addressString.substring(1):"jalr "+addressString)
    );
    log.info("Left vCall: "+vCall.sourcePos.line);
  }

  @Override
  public void visit(VBuiltIn vBuiltIn) throws Throwable {
    log.info("Entered vBuiltIn: "+vBuiltIn.sourcePos.line);

    if (vBuiltIn.op == VBuiltIn.Op.Error) {
      String msg = vBuiltIn.args[0].toString();
      String messageLabel = (msg.equals("\"null pointer\""))?"_str0":"_str1";
      mipsProgram.add("la $a0 "+messageLabel); // load the value of _str into $a0
      mipsProgram.add("j _error"); // jump to target address of _error

    } else if (vBuiltIn.op == VBuiltIn.Op.PrintIntS) {
      String arg = vBuiltIn.args[0].toString();
      if (isNumeric(arg)) {
        mipsProgram.add("li $a0 "+arg);
      } else {
        mipsProgram.add("move $a0 " + arg);
      }
      mipsProgram.add("jal _print");

    } else if (vBuiltIn.op == VBuiltIn.Op.HeapAllocZ) {
      String arg = vBuiltIn.args[0].toString();
      if (isNumeric(arg)) {
        mipsProgram.add("li $a0 "+arg);
      } else { // arg is a register
        mipsProgram.add("move $a0 "+arg);
      }
      mipsProgram.add("jal _heapAlloc");
      mipsProgram.add("move "+vBuiltIn.dest.toString()+" $v0");

    } else { // for Add(), LtS, MulS, Sub
      /**
       * These can be : $t1 = LtS(1 2)
       *              | $t1 = LtS($t1 2)
       * All of these functions will always take 2 arguments, this is not vCall. If both the arguments are
       * immediates, store the first immediate into a reserved register - this is because the provided examples do it
       * this way.
       */
      Iterator<String> reserveRegisterIterator = reservedRegister.iterator();
      String dstString = vBuiltIn.dest.toString();
      String a1 = vBuiltIn.args[0].toString();
      String a2 = vBuiltIn.args[1].toString();
      String register = isNumeric(a1)? reserveRegisterIterator.next() : null;

      if (vBuiltIn.op == VBuiltIn.Op.Add) {
        if (register != null) {//a1 is numerical
          if (isNumeric(a2)) { // both a1 and a2 are numerical
            int i1 = Integer.parseInt(a1);
            int i2 = Integer.parseInt(a2);
            mipsProgram.add("li "+dstString+" "+(i1+i2)+" #"+vBuiltIn.sourcePos.line);
          } else {// only a1 is numerical, but a2 is register
            mipsProgram.add("li "+register+" "+a1);
            mipsProgram.add("addu "+dstString+" "+register+" "+a2);
          }
        } else {// a1 is a register, and a2 can either be a register or a number\
          mipsProgram.add("addu "+dstString+" "+a1+" "+a2);
        }

      } else if (vBuiltIn.op == VBuiltIn.Op.LtS) {
        if (register != null) {//a1 is numerical
          if (isNumeric(a2)) { // both a1 and a2 are numerical
            int i1 = Integer.parseInt(a1);
            int i2 = Integer.parseInt(a2);
            mipsProgram.add("li " + dstString + " " + (i1 < i2 ? 1 : 0) + " #" + vBuiltIn.sourcePos.line);
          } else {// only a1 is numerical, but a2 is register
            mipsProgram.add("li " + register + " " + a1);
            mipsProgram.add("slt " + dstString + " " + register + " " + a2);
          }
        } else {// a1 is a register, and a2 can either be a register or a number
          mipsProgram.add((isNumeric(a2) ? "slti " : "slt ") + dstString + " " + a1 + " " + a2);
        }

      } else if (vBuiltIn.op == VBuiltIn.Op.Lt) {
        if (register != null) {//a1 is numerical
          if (isNumeric(a2)) { // both a1 and a2 are numerical
            int i1 = Integer.parseInt(a1);
            int i2 = Integer.parseInt(a2);
            mipsProgram.add("li " + dstString + " " + (i1 < i2 ? 1 : 0) + " #" + vBuiltIn.sourcePos.line);
          } else {// only a1 is numerical, but a2 is register
            mipsProgram.add("li " + register + " " + a1);
            mipsProgram.add("sltu " + dstString + " " + register + " " + a2);
          }
        } else {// a1 is a register, and a2 can either be a register or a number
          mipsProgram.add((isNumeric(a2) ? "sltui " : "sltu ") + dstString + " " + a1 + " " + a2);
        }

      } else if (vBuiltIn.op == VBuiltIn.Op.MulS) {
        if (register != null) {//a1 is numerical
          if (isNumeric(a2)) { // both a1 and a2 are numerical
            int i1 = Integer.parseInt(a1);
            int i2 = Integer.parseInt(a2);
            mipsProgram.add("li "+dstString+" "+(i1*i2));
          } else {// only a1 is numerical, but a2 is register
            mipsProgram.add("li "+register+" "+a1);
            mipsProgram.add("mul "+dstString+" "+register+" "+a2);
          }
        } else {// a1 is a register, and a2 can either be a register or a number
          mipsProgram.add("mul "+dstString+" "+a1+" "+a2);
        }

      } else if (vBuiltIn.op == VBuiltIn.Op.Sub) {
        if (register != null) {// a1 is numerical
          if (isNumeric(a2)) { // both a1 and a2 are numerical
            int i1 = Integer.parseInt(a1);
            int i2 = Integer.parseInt(a2);
            mipsProgram.add("li "+dstString+" "+(i1-i2));
          } else { // only a1 is numerical, but a2 is register
            mipsProgram.add("li "+register+" "+a1);
            mipsProgram.add("subu "+dstString+" "+register+" "+a2);
          }
        } else {// a1 is a register, and a2 can either be a register or a number
          mipsProgram.add("subu "+dstString+" "+a1+" "+a2);
        }
      }

    }

    log.info("Left vBuiltIn: "+vBuiltIn.sourcePos.line);
  }

  /**
   * These are of the form: [a+4] = v
   *                      | local[0] = $s0
   *                      | out[0] = 4
   *                      | out[1] = :vmt_Operator
   *                      | [$t0] = :vmt_Fac
   *                      | [$t0+0] = $t1
   *                      | [$t0] = 90
   */
  @Override
  public void visit(VMemWrite vMemWrite) throws Throwable {
    log.info("Entered vMemWrite: "+vMemWrite.sourcePos.line);
    Iterator<String> reservedRegisterIterator = reservedRegister.iterator();
    String source = vMemWrite.source.toString();
    final int offset = (vMemWrite.dest instanceof VMemRef.Stack)?-9999999:((VMemRef.Global) vMemWrite.dest).byteOffset;

    if (vMemWrite.dest instanceof VMemRef.Stack) { // local[0] = $s0 | out[0] = 4  | out[1] = :vmt_Operator
      VMemRef.Stack dstStack = (VMemRef.Stack)vMemWrite.dest;
      if (isNumeric(source)) {
        String register = reservedRegisterIterator.next();
        mipsProgram.add("li "+register+" "+source);
        if (dstStack.region == VMemRef.Stack.Region.Local) {
          mipsProgram.add("sw " + register + " " + -1*(4*dstStack.index+12) + "($fp)");
        } else if (dstStack.region == VMemRef.Stack.Region.In) {
          mipsProgram.add("sw " + register + " " + (4*dstStack.index) + "($fp)");
        } else if (dstStack.region == VMemRef.Stack.Region.Out) {
          mipsProgram.add("sw " + register + " " + (4*dstStack.index) + "($sp)");
        }
      } else {
        if (source.charAt(0)==':') { // source is an address
          String register = reservedRegisterIterator.next();
          mipsProgram.add("la "+register+" "+source.substring(1));
          if (dstStack.region == VMemRef.Stack.Region.Local) {
            mipsProgram.add("sw " + register + " " + -1*(4*dstStack.index+12) + "($fp)");
          } else if (dstStack.region == VMemRef.Stack.Region.In) {
            mipsProgram.add("sw " + register + " " + (4*dstStack.index) + "($fp)");
          } else if (dstStack.region == VMemRef.Stack.Region.Out) {
            mipsProgram.add("sw " + register + " " + (4*dstStack.index) + "($sp)");
          }
        } else { // register
          if (dstStack.region == VMemRef.Stack.Region.Local) {
            mipsProgram.add("sw " + source + " " + -1*(4*dstStack.index+12) + "($fp)");
          } else if (dstStack.region == VMemRef.Stack.Region.In) {
            mipsProgram.add("sw " + source + " " + (4*dstStack.index) + "($fp)");
          } else if (dstStack.region == VMemRef.Stack.Region.Out) {
            mipsProgram.add("sw " + source + " " + (4*dstStack.index) + "($sp)");
          }
        }
      }

    } else { // destination is in register
      String baseString = ((VMemRef.Global)vMemWrite.dest).base.toString();
      if (source.charAt(0)==':') { // source is an address: [$t0] = :vmt_Fac
        String register = reservedRegisterIterator.next();
        mipsProgram.add("la "+register+" "+source.substring(1));
        mipsProgram.add("sw "+register+" "+offset+"("+baseString+")"); // Copy from register to memory

      } else { // source is a register or number: [$t0+0] = $t1   or    [$t0] = 90
        if (isNumeric(source)) {
          String register = reservedRegisterIterator.next();
          if (Integer.parseInt(source) == 0) { // use the $0 register
            mipsProgram.add("sw $0 " + offset + "(" + baseString + ")");
          } else {
            mipsProgram.add("li " + register + " " + source);
            mipsProgram.add("sw " + register + " " + offset + "(" + baseString + ")");
          }
        } else {
          mipsProgram.add("sw "+source+" "+offset+"("+baseString+")");
        }
      }
    }

    log.info("Left vMemWrite: "+vMemWrite.sourcePos.line);
  }

  /**
   * These are of the form: $t1 = [$t0+0]
   *                      | $s0 = local[0]
   *                      | $t5 = in[5]
   *                      | $t6 = out[12]
   */
  @Override
  public void visit(VMemRead vMemRead) throws Throwable {
    log.info("Entered vMemRead: "+vMemRead.sourcePos.line);
    Iterator<String> reservedRegisterIterator = reservedRegister.iterator();
    String dstReg = vMemRead.dest.toString();
    if (vMemRead.source instanceof VMemRef.Stack) {
      VMemRef.Stack srcStack = ((VMemRef.Stack) vMemRead.source);
      if (srcStack.region == VMemRef.Stack.Region.Local) {
        mipsProgram.add("lw " + dstReg + " " + -1*(4*srcStack.index+12) + "($fp)");
      } else if (srcStack.region == VMemRef.Stack.Region.In) {
        mipsProgram.add("lw " + dstReg + " " + (4*srcStack.index) + "($fp)");
      } else if (srcStack.region == VMemRef.Stack.Region.Out) {
        mipsProgram.add("lw " + dstReg + " " + (4*srcStack.index) + "($sp)");
      }
    } else {
      final int offset = ((VMemRef.Global)vMemRead.source).byteOffset;
      mipsProgram.add(
          "lw "+dstReg+" "+offset+"("+(((VMemRef.Global) vMemRead.source).base.toString())+")"
      );
    }
    log.info("Left vMemRead: "+vMemRead.sourcePos.line);
  }

  /**
   * These are always: if0 $t1 goto :label
   *                 | if $t0 goto :label
   */
  @Override
  public void visit(VBranch vBranch) throws Throwable {
    log.info("Entered vBranch: "+ vBranch.sourcePos.line);
    mipsProgram.add(
        ((vBranch.positive)?"bnez ":"beqz ")+vBranch.value.toString()+" "+vBranch.target.toString().substring(1)
    );
    log.info("Left vBranch: "+vBranch.sourcePos.line);
  }

  @Override
  public void visit(VGoto vGoto) throws Throwable {
    log.info("Entered vGoto: "+vGoto.sourcePos.line);
    mipsProgram.add("j "+vGoto.target.toString().substring(1));
    log.info("Left vGoto: "+vGoto.sourcePos.line);
  }

  @Override
  public void visit(VReturn vReturn) throws Throwable {
    log.info("Entered vReturn: "+vReturn.sourcePos.line);
    log.info("Nothing to do for return.");
    log.info("Left vReturn: "+vReturn.sourcePos.line);
  }

  private List<String> PrintIntS() {
    String code = "_print:\n" +
        "  li $v0 1   # syscall: print integer\n" +
        "  syscall\n" +
        "  la $a0 _newline\n" +
        "  li $v0 4   # syscall: print string\n" +
        "  syscall\n" +
        "  jr $ra\n";
    return Arrays.asList(code.split("\n"));
  }

  private List<String> Error() {
    String code = "_error:\n" +
        "  li $v0 4   # syscall: print string\n" +
        "  syscall\n" +
        "  li $v0 10  # syscall: exit\n" +
        "  syscall\n";
    return Arrays.asList(code.split("\n"));
  }

  private List<String> HeapAlloc() {
    String code = "_heapAlloc:\n" +
        "  li $v0 9   # syscall: sbrk\n" +
        "  syscall\n" +
        "  jr $ra\n";
    return Arrays.asList(code.split("\n"));
  }

  private List<String> Epilogue() {
    String code = ".data\n" +
        ".align 0\n" +
        "_newline: .asciiz \"\\n\"\n" +
        "_str0: .asciiz \"null pointer\\n\"\n" +
        "_str1: .asciiz \"array index out of bounds\\n\"";
    return Arrays.asList(code.split("\n"));
  }

  private List<String> Prologue() {
    String code = ".text\n" +
        "\n" +
        "  jal Main\n" +
        "  li $v0 10\n" +
        "  syscall\n";
    return Arrays.asList(code.split("\n"));
  }

  private List<String> FunctionPrologue() {
    String code = "sw $fp -8($sp)\n" +
        "move $fp $sp\n" +
        "subu $sp $sp "+frameSize+"\n" +
        "sw $ra -4($fp)";
    return Arrays.asList(code.split("\n"));
  }

  private List<String> FunctionEpilogue() {
    String code = "lw $ra -4($fp)\n" +
        "lw $fp -8($fp)\n" +
        "addu $sp $sp "+frameSize+"\n" +
        "jr $ra\n";
    return Arrays.asList(code.split("\n"));
  }
}
