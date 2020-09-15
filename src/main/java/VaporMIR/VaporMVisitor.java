package VaporMIR;

import VaporMIR.RegisterAllocation.LinearScanRegisterAllocation;
import VaporMIR.Storage.Register.Register;
import VaporMIR.Storage.Stack.StackStorage;
import VaporMIR.Storage.Storage;
import cs132.vapor.ast.*;
import core.util.LOGGER;

import java.util.*;

import static core.util.util.isNumeric;

public class VaporMVisitor extends VInstr.VisitorPR<Object, Object, Throwable> {
  // for logging
  private static final transient LOGGER log = new LOGGER(VaporMVisitor.class.getSimpleName(), true);

  public VaporMProgram vaporMProgram = new VaporMProgram();
  private LinearScanRegisterAllocation linearScanRegisterAllocation;
  private TreeMap<Integer, VInstr> lineInstructionMap = new TreeMap<>();
  public int finalStackSize = 0;

  /**
   * Stores a mapping between the reserved register and what it stores
   */
  private Map<String, String> reservedRegisters = new HashMap<String, String>(){{
    put("$s6", null);
    put("$s7", null);
    put("$v1", null);
  }};

  /**
   * Constructor
   * @param prog This is the input vapor program
   */
  public VaporMVisitor(VaporProgram prog) throws Throwable{
    // Copy the var data segment from the original vapor program,
    // this data segment only contains the pointers to the functions
    for (VDataSegment dataSegment : prog.dataSegments) {
      vaporMProgram.add("const "+dataSegment.ident);
      vaporMProgram.indent();
      for (VOperand.Static funcPtrs : dataSegment.values) {
        vaporMProgram.add(funcPtrs.toString());
      }
      vaporMProgram.outdent();
      vaporMProgram.addNewLine();
    }

    // Iterate over all functions
    for (VFunction func : prog.functions) {
      // first iterate over all the functions and fill the lineInstructionMap. This is to make sure that after the
      // VBranch visitor, if the next instruction is VBuiltIn.op.Error we write 'if' instead of 'if0' to the final
      // vaporM program
      for (VInstr instr : func.body)
        lineInstructionMap.put(instr.sourcePos.line, instr);

      linearScanRegisterAllocation = new LinearScanRegisterAllocation(func);

      // we need to first know the line numbers of where the code label occurs since code label is not an instruction
      // and there is no VCodeLabel visitor in VInstructor, and also there is no way in JAVA to inherit both the
      // VInstr and VCodeLabel, i.e. JAVA does not support multiple inheritance
      Map<Integer, VCodeLabel> codeLabelLineMap = new HashMap<Integer, VCodeLabel>(){{
        for (VCodeLabel label : func.labels) put(label.sourcePos.line, label);
      }};

      finalStackSize = linearScanRegisterAllocation.localStackMap.size()
          +linearScanRegisterAllocation.usedCalleeRegister.size();

      String functionDeclaration = "func " + func.ident + " [in ";
      functionDeclaration += (func.params.length>4) ? func.params.length-4 : 0;
      functionDeclaration += ", out ";
      functionDeclaration += linearScanRegisterAllocation.controlFlowGraph.outStackSize;
      functionDeclaration += ", local ";
      functionDeclaration += (finalStackSize+17);

      functionDeclaration += "]";
      vaporMProgram.add(functionDeclaration);

      vaporMProgram.indent();
      // backup the callee saved registers
      for (Register i : linearScanRegisterAllocation.usedCalleeRegister.keySet()) {
        vaporMProgram.add(linearScanRegisterAllocation.usedCalleeRegister.get(i)+" = "+i);
      }

      // the examples seem to store the value in argument registers into caller and callee registes
      for (int i=0; i<func.params.length; ++i) {
        Storage p = linearScanRegisterAllocation.variableMap.get(func.params[i].ident);
        Iterator<String> reservedRegistersInterator = reservedRegisters.keySet().iterator();
        if (p==null) continue;
        if (i<4) {
            vaporMProgram.add(p + " = $a" + i);
        } else {
          if (p instanceof StackStorage) {
            String register = reservedRegistersInterator.next();
            vaporMProgram.add(register+" = in["+(i-4)+"]");
            vaporMProgram.add(p+" = "+register);
          } else {
            vaporMProgram.add(p + " = in[" + (i - 4) + "]");
          }
        }
      }
      vaporMProgram.outdent();

      // Iterate over every instruction in the function
      int previousLine = func.sourcePos.line;
      for (VInstr instr : func.body) {
        final int currentLine = instr.sourcePos.line;
        for (int i=previousLine; i<currentLine; ++i)
          if (codeLabelLineMap.containsKey(i)) {
            vaporMProgram.indent();
            vaporMProgram.add(codeLabelLineMap.get(i).ident+":");
            vaporMProgram.outdent();
          }
        previousLine = currentLine;
          vaporMProgram.indent();
        instr.accept(null, this);
        vaporMProgram.outdent();
      }

      vaporMProgram.addNewLine();
    }
  }


  /**
   * ------------------------------------------------------------------------------------------------------------------
   * Auto Generated Methods
   * ------------------------------------------------------------------------------------------------------------------
   */


  /**
   * These are: t.0 = 1 | t.1 = :address_name
   */
  @Override
  public Object visit(Object o, VAssign vAssign) throws Throwable {
    log.info("Entered VAssign. line: "+vAssign.sourcePos);
    vaporMProgram.indent();
    String code = "";
    String lhsString = vAssign.dest.toString();
    String rhsString = vAssign.source.toString();
    Storage lhs = linearScanRegisterAllocation.variableMap.get(lhsString);
    Storage rhs = linearScanRegisterAllocation.variableMap.get(rhsString);
    Iterator<String> reservedRegs = reservedRegisters.keySet().iterator();


    if (rhs == null) {
      code += rhsString;
    } else {
      if (rhs instanceof StackStorage) {
        String reg = reservedRegs.next();
        vaporMProgram.add(reg+" = "+rhs);
        code += reg;
      } else {
        code += rhs;
      }
    }

    if (lhs == null) {
      if (!linearScanRegisterAllocation.ignoredVariables.contains(lhsString)) {
        code = lhsString + " = " + code;
        vaporMProgram.add(code);
      } else {
        vaporMProgram.outdent();
        return null;
      }
    } else {
      if (lhs instanceof StackStorage) {
        String reg = reservedRegs.next();
        code = reg + " = " + code;
        vaporMProgram.add(code);
        vaporMProgram.add(lhs+" = "+reg);
      } else {
        code = lhs + " = " + code;
        vaporMProgram.add(code);
      }
    }

    vaporMProgram.outdent();
    vaporMProgram.addNewLine();
    log.info("Left VAssign.  line: "+vAssign.sourcePos);
    return null;
  }

  /**
   * These are of the form: t.0 = call t.1(t.2 t.3 t.4 t.5 t.6 ...)
   */
  @Override
  public Object visit(Object o, VCall vCall) throws Throwable {
    log.info("Entered VCall. line: "+vCall.sourcePos);
    vaporMProgram.indent();
    Iterator<String> reservedRegisterIterator = reservedRegisters.keySet().iterator();

    // backup everything before the call statement
    BackupCalleeRegisters();
    BackupCallerRegisters();
    BackupReservedRegisters();


    String register = null;
    for (int i=0; i<vCall.args.length; ++i) {
      String arg = vCall.args[i].toString();
      Storage argumentRegister = linearScanRegisterAllocation.variableMap.get(arg);
      if (i<4) {
        if (argumentRegister == null) { // wither number or data segment
          vaporMProgram.add("$a"+i+" = "+arg+"  //"+vCall.sourcePos.line);
        } else {
          vaporMProgram.add("$a"+i+" = "+argumentRegister+"  //"+vCall.sourcePos.line);
        }

      } else {
        final String instr;
        if (argumentRegister == null) { // either datasegment or number
          instr ="out[" + (i - 4) + "] = " + arg + "  //" + vCall.sourcePos.line;
        } else {
          instr ="out[" + (i - 4) + "] = " + argumentRegister + "  //" + vCall.sourcePos.line;
        }
        if (argumentRegister == null) { // datasegments and numbers
          vaporMProgram.add(instr);
        } else {
          if (argumentRegister instanceof StackStorage) {
            if (register==null) register = reservedRegisterIterator.next();
            vaporMProgram.add(register+" = "+argumentRegister);
            vaporMProgram.add("out["+(i-4)+"] = "+register+"  //"+vCall.sourcePos.line);
          } else { // this means that my argument is in register
            vaporMProgram.add(instr);
          }
        }
      }
    }
    reservedRegisterIterator = reservedRegisters.keySet().iterator();

    String callAddress = vCall.addr.toString();
    Storage callAddressRegister = linearScanRegisterAllocation.variableMap.get(callAddress);
    if (callAddressRegister == null) { // datasegment
      vaporMProgram.add("call "+callAddress+"    //"+vCall.sourcePos.line);
    } else {
      if (callAddressRegister instanceof StackStorage) {
        register = reservedRegisterIterator.next();
        vaporMProgram.add(register+" = "+callAddressRegister);
        vaporMProgram.add("call "+register);
      } else {
        vaporMProgram.add("call "+callAddressRegister);
      }
    }
    reservedRegisterIterator = reservedRegisters.keySet().iterator();


    // Restore every backed up register after the call
    RestoreCalleeRegisters();
    RestoreCallerRegisters();
    RestoreReserveRegisters();



    if (vCall.dest != null) {
      String dstString = vCall.dest.ident;
      Storage dstReg = linearScanRegisterAllocation.variableMap.get(dstString);
      if (dstReg == null) {
        if (!linearScanRegisterAllocation.ignoredVariables.contains(dstString)) {
          vaporMProgram.add(dstReg+" = $v0");
        }
      } else {
        vaporMProgram.add(dstReg+" = $v0");
      }
    }

    vaporMProgram.outdent();
    vaporMProgram.addNewLine();
    log.info("Left VCall. line: "+vCall.sourcePos);
    return null;
  }

  private void BackupCalleeRegisters() {
    for (int i=finalStackSize; i<finalStackSize+7; ++i) {
      int j = i-finalStackSize;
      vaporMProgram.add("local["+i+"] = $s"+j);
    }
  }

  private void RestoreCalleeRegisters() {
    for (int i=finalStackSize; i<finalStackSize+7; ++i) {
      int j = i-finalStackSize;
      vaporMProgram.add("$s"+j+" = local["+i+"]");
    }
  }

  private void BackupCallerRegisters() {
    for (int i=finalStackSize+7; i<finalStackSize+7+9; ++i) {
      int j = i-finalStackSize-7;
      vaporMProgram.add("local["+i+"] = $t"+j);
    }
  }

  private void RestoreCallerRegisters() {
    for (int i=finalStackSize+7; i<finalStackSize+7+9; ++i) {
      int j = i-finalStackSize-7;
      vaporMProgram.add("$t"+j+" = local["+i+"]");
    }
  }

  private void BackupReservedRegisters() {
    vaporMProgram.add("local["+(finalStackSize+16)+"] = $v1");
  }

  private void RestoreReserveRegisters() {
    vaporMProgram.add("$v1 = local["+(finalStackSize+16)+"]");
  }

  @Override
  public Object visit(Object o, VBuiltIn vBuiltIn) throws Throwable {
    log.info("Entered VBuiltIn. line: "+vBuiltIn.sourcePos);
    vaporMProgram.indent();
    Iterator<String> reservedRegisterIterator = reservedRegisters.keySet().iterator();

    String code = vBuiltIn.op.name+"(";

    Map<String, String> usedReservedRegisters = new HashMap<>();
    for (int i=0; i<vBuiltIn.args.length; ++i) {
      String arg = vBuiltIn.args[i].toString();
      if (isNumeric(arg) || arg.charAt(0)=='"') {
        code += arg;
      } else {
        Storage a = linearScanRegisterAllocation.variableMap.get(arg);
        if (a instanceof StackStorage) {
          String register = reservedRegisterIterator.next();
          vaporMProgram.add(register+" = "+a);
          code += register;
          usedReservedRegisters.put(register, a.toString());
        } else {
          code += a.toString();
        }
      }
      if (i+1 != vBuiltIn.args.length) code += " ";
    }
    code += ")    //"+vBuiltIn.sourcePos.line;

    if (vBuiltIn.dest != null) {
      Storage a = linearScanRegisterAllocation.variableMap.get(vBuiltIn.dest.toString());
      if (a instanceof StackStorage) {
        String register = reservedRegisterIterator.next();
        code = register+" = "+code;
        vaporMProgram.add(code);
        vaporMProgram.add(a+" = "+register);
      } else {
        code = a+" = "+code;
        vaporMProgram.add(code);
      }
    } else {
      vaporMProgram.add(code);
    }

    vaporMProgram.outdent();
    vaporMProgram.addNewLine();
    log.info("Left VBuiltIn. line: "+vBuiltIn.sourcePos);
    return null;
  }

  /**
   * Memory write instruction. Ex: "[a+4] = v".
   */
  @Override
  public Object visit(Object o, VMemWrite vMemWrite) throws Throwable {
    log.info("Entered VMemWrite. line: "+vMemWrite.sourcePos);
    String code = "";
    String baseString = ((VMemRef.Global)vMemWrite.dest).base.toString();
    Storage baseRegister = linearScanRegisterAllocation.variableMap.get(baseString);
    int offset = ((VMemRef.Global) vMemWrite.dest).byteOffset;
    String src = vMemWrite.source.toString();
    Storage srcRegister = linearScanRegisterAllocation.variableMap.get(src);
    Iterator<String> usedReservedRegistersIterator = reservedRegisters.keySet().iterator();

    vaporMProgram.indent();
    if (srcRegister == null) {
      code += src;
    } else {
      if (srcRegister instanceof StackStorage) {
        String register = usedReservedRegistersIterator.next();
        vaporMProgram.add(register+" = "+srcRegister);
        code += register;
      } else {
        code += srcRegister;
      }
    }


    if (baseRegister == null) {
      code = "["+baseString+"+"+offset+"] = "+code;
      vaporMProgram.add(code);
    } else {
      if (baseRegister instanceof StackStorage) {
        String register = usedReservedRegistersIterator.next();
        vaporMProgram.add(register+" = "+baseRegister);
        code = "["+register+"+"+offset+"] = "+code;
        vaporMProgram.add(code);
      } else {
        code = "["+baseRegister+"+"+offset+"] = "+code;
        vaporMProgram.add(code);
      }
    }


    vaporMProgram.outdent();
    vaporMProgram.addNewLine();
    log.info("Left VMemWrite. line: "+vMemWrite.sourcePos);
    return null;
  }

  /**
   * Memory read instructions. Ex: "v = [a+4]"
   */
  @Override
  public Object visit(Object o, VMemRead vMemRead) throws Throwable {
    log.info("Entered VMemRead. line: "+vMemRead.sourcePos);
    vaporMProgram.indent();
    Iterator<String> reservedRegisterIterator = reservedRegisters.keySet().iterator();
    String code = "";
    String dstStr = vMemRead.dest.toString();
    Storage dstRegister = linearScanRegisterAllocation.variableMap.get(dstStr);
    String srcBaseString = ((VMemRef.Global)vMemRead.source).base.toString();
    Storage srcBaseRegister = linearScanRegisterAllocation.variableMap.get(srcBaseString);
    int offset = ((VMemRef.Global)vMemRead.source).byteOffset;


    if (srcBaseRegister == null) {
      code += "["+srcBaseString+"+"+offset+"]";
    } else {
      if (srcBaseRegister instanceof StackStorage) {
        String register = reservedRegisterIterator.next();
        vaporMProgram.add(register+" = "+srcBaseRegister+"    //"+vMemRead.sourcePos.line);
        code += "["+register+"+"+offset+"]";
      } else {
        code += "[" + srcBaseRegister + "+" + offset + "]";
      }
    }


    //
    if (dstRegister == null) {
      code = dstStr + " = " + code;
      vaporMProgram.add(code);
    } else {
      if (dstRegister instanceof StackStorage) {
        String register = reservedRegisterIterator.next();
        code = register+" = "+code;
        vaporMProgram.add(code);
        vaporMProgram.add(dstRegister+" = "+register);
      } else {
        code = dstRegister + " = " + code;
        vaporMProgram.add(code);
      }
    }

    vaporMProgram.outdent();
    vaporMProgram.addNewLine();
    log.info("Left VMemRead. line: "+vMemRead.sourcePos);
    return null;
  }

  /**
   * These are: if0 t.0 goto :branch | if t.0 goto: branch
   */
  @Override
  public Object visit(Object o, VBranch vBranch) throws Throwable {
    log.info("Entered VBranch. line: "+vBranch.sourcePos);
    Iterator<String> reserveRegisterIterator = reservedRegisters.keySet().iterator();
    String conditionString = vBranch.value.toString();
    Storage conditionRegister = linearScanRegisterAllocation.variableMap.get(conditionString);
    vaporMProgram.indent();

    if (conditionRegister == null) {
      vaporMProgram.add("if"+(vBranch.positive?"":0)+" "+conditionString+" goto :"+vBranch.target.ident+"  //"+vBranch.sourcePos.line);
    } else {
      if (conditionRegister instanceof StackStorage) {
        String register = reserveRegisterIterator.next();
        vaporMProgram.add(register+" = "+conditionRegister);
        vaporMProgram.add("if"+(vBranch.positive?"":0)+" "+register+" goto :"+vBranch.target.ident+"  //"+vBranch.sourcePos.line);
      } else {
        vaporMProgram.add("if"+(vBranch.positive?"":0)+" "+conditionRegister+" goto :"+vBranch.target.ident+"  //"+vBranch.sourcePos.line);
      }
    }

    vaporMProgram.outdent();
    vaporMProgram.addNewLine();
    log.info("Left VBranch. line: "+vBranch.sourcePos);
    return null;
  }

  @Override
  public Object visit(Object o, VGoto vGoto) throws Throwable {
    log.info("Entered VGoto. line: "+vGoto.sourcePos);

    vaporMProgram.indent();
    vaporMProgram.add("goto "+vGoto.target.toString()+"  //"+vGoto.sourcePos.line);
    vaporMProgram.outdent();
    vaporMProgram.addNewLine();
    log.info("Left VGoto."+vGoto.sourcePos);
    return null;
  }

  @Override
  public Object visit(Object o, VReturn vReturn) throws Throwable {
    log.info("Entered VReturn. line: "+vReturn.sourcePos);
    vaporMProgram.indent();

    log.info("Checking return register: "+vReturn.value);
    if (vReturn.value != null) {
      Storage returnRegister = linearScanRegisterAllocation.variableMap.get(vReturn.value.toString());
      // we can return an address or a number, either of these will not be in the variableMap in linearScanRegisterAllocation
      vaporMProgram.add("$v0 = "+(returnRegister==null?vReturn.value:returnRegister)+"  //"+vReturn.sourcePos.line);
    }

    // In the provided example, the callee register restoration seems to happen before the ret statement
    for (Register i : linearScanRegisterAllocation.usedCalleeRegister.keySet()) {
      vaporMProgram.add(i+" = "+linearScanRegisterAllocation.usedCalleeRegister.get(i)+"  //"+vReturn.sourcePos.line);
    }

    vaporMProgram.add("ret  //"+vReturn.sourcePos.line);


    vaporMProgram.outdent();
    vaporMProgram.addNewLine();
    log.info("Left VReturn. line: "+vReturn.sourcePos);
    return null;
  }
}
