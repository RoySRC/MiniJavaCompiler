package VaporIR.VaporVisitor;

import VaporIR.Factory.LabelFactory.*;
import VaporIR.HelperVariableContainers.Function;
import VaporIR.HelperVariableContainers.MsgSendVars;
import VaporIR.SymbolTableVisitor.Binding;
import VaporIR.SymbolTableVisitor.ReservedKeywords;
import VaporIR.SymbolTableVisitor.SymTable;
import TypeChecking.TypeChecking.TypeCheckException;
import core.syntaxtree.*;
import core.util.LOGGER;
import core.visitor.GJDepthFirst;

import java.util.*;

import static core.util.util.isClass;

public class FunctionVisitor extends GJDepthFirst<String, String> {
  // Logger
  private static final transient LOGGER log = new LOGGER(FunctionVisitor.class.getSimpleName());

  private SymTable symbolTable = null;
  private final SymTable rootSymTable;
  private LinkedList<String> finalVaporCode = null;
  private String indentation = "";

  private TMPVAR tmp = (TMPVAR) LabelFactory.getInstance().createLabel(LabelFactory.TYPE.TMPVAR);
  private int OFFSET = 0;
  private String globalType = null;

  // Stacks
  private Stack<SymTable> currentClassStack = new Stack<>();
  private Map<String, LinkedList<String>> inheritanceLinks = null;

  private MsgSendVars msgVars = new MsgSendVars();
  
  public FunctionVisitor(SymTable symTable, Map<String, LinkedList<String>> inheritance, LinkedList<String> finalVaporCode) {
    this.symbolTable = symTable;
    this.rootSymTable = symTable;
    this.finalVaporCode = finalVaporCode;
    this.inheritanceLinks = inheritance;
  }

  /**
   * Setter for the globalType
   * @param type The type of the global type
   */
  private void setGlobalType(String type) {
    int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
    globalType = type;
    log.info(String.format("Global Type set to: \"%s\"", globalType), line);
  }

  public void print() {
    for (String line : finalVaporCode) {
      System.out.println(line);
    }
  }

  private void setOFFSET(int i) {
    int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
    this.OFFSET = i;
    log.info("New OFFSET set to: "+OFFSET, line);
  }

  private void Indent() {
    this.indentation += "  ";
  }

  private void Outdent() {
    if (indentation.length() > 1)
      indentation = indentation.substring(0, indentation.length()-2);
  }

  private void addToVaporCode(String statement) {
    int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
    String comment = "";//" //"+line;
    this.finalVaporCode.add(indentation+statement+comment);
    log.info("Added: "+finalVaporCode.getLast()+"; to vapor code", line);
  }


  /**
   * User defined methods starts here
   */


  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(Goal n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    n.f0.accept(this, argu);
    n.f1.accept(this, argu);    
    log.info("Left "+n.getClass().getSimpleName());
    AllocArray();
    return null;
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> "public"
   * f4 -> "static"
   * f5 -> "void"
   * f6 -> "main"
   * f7 -> "("
   * f8 -> "String"
   * f9 -> "["
   * f10 -> "]"
   * f11 -> Identifier()
   * f12 -> ")"
   * f13 -> "{"
   * f14 -> ( VarDeclaration() )*
   * f15 -> ( Statement() )*
   * f16 -> "}"
   * f17 -> "}"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(MainClass n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      symbolTable = symbolTable.getSymTable(n.f1.f0.tokenImage); // move to the class scope
      currentClassStack.push(symbolTable);
      symbolTable = symbolTable.getSymTable("main()"); // move to the main method symbol table
      // Add the main function declaration
      this.addToVaporCode("func Main()");

      n.f14.accept(this, argu);

      // add the statements
      this.Indent();
      for (Node stmt : n.f15.nodes) {
        stmt.accept(this, argu);
      }
      this.Outdent();

      // Add the return statement
      this.Indent();
      this.addToVaporCode("ret");
      this.Outdent();

      this.addToVaporCode("\n");
      tmp.reset();
      symbolTable = symbolTable.getParent().getParent();
      currentClassStack.clear();
    } catch (TypeCheckException e) {
      log.printStackTrace(e);
    }
    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> ClassDeclaration()
   * | ClassExtendsDeclaration()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(TypeDeclaration n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    n.f0.accept(this, argu);
    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(ClassDeclaration n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      // move to the class scope
      symbolTable = symbolTable.getSymTable(n.f1.f0.tokenImage);
      currentClassStack.push(symbolTable);

      n.f3.accept(this, argu);

      for (Node method : n.f4.nodes) {
        method.accept(this, argu);
      }

      // move back to the original symbol table
      symbolTable = symbolTable.getParent();
      currentClassStack.clear();
    } catch (Exception e) {
      log.printStackTrace(e);
    }
    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * TODO
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "extends"
   * f3 -> Identifier()
   * f4 -> "{"
   * f5 -> ( VarDeclaration() )*
   * f6 -> ( MethodDeclaration() )*
   * f7 -> "}"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(ClassExtendsDeclaration n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      // move to the class scope
      symbolTable = symbolTable.getSymTable(n.f1.f0.tokenImage);
      currentClassStack.push(symbolTable);

      n.f5.accept(this, argu);

      for (Node method : n.f6.nodes) {
        method.accept(this, argu);
      }

      symbolTable = symbolTable.getParent();
      currentClassStack.clear();
    } catch (Exception e) {
      log.printStackTrace(e);
    }
    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(VarDeclaration n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    n.f0.accept(this, argu); // visiting this node will also set the global type

    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> "public"
   * f1 -> Type()
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( FormalParameterList() )?
   * f5 -> ")"
   * f6 -> "{"
   * f7 -> ( VarDeclaration() )*
   * f8 -> ( Statement() )*
   * f9 -> "return"
   * f10 -> Expression()
   * f11 -> ";"
   * f12 -> "}"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(MethodDeclaration n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f1.accept(this, argu);
      String return_type = globalType;

      // declare the function signature
      String paramRest = n.f4.accept(this, argu);
      paramRest = (paramRest == null) ? "" : paramRest;
      log.info(paramRest);
      addToVaporCode("func "+symbolTable.getName()+"."+n.f2.f0.tokenImage+"(this"+paramRest+")");

      // add the variables
      this.Indent();
      for (Node variables : n.f7.nodes) {
        variables.accept(this, argu);
      }
      this.Outdent();


      // move to the function symbol table
      symbolTable = symbolTable.getSymTable(n.f2.f0.tokenImage+"()");
      log.info("Current symbol table pointing to: "+symbolTable.getName());

      // generate vapor code for the statements
      this.Indent();
      for (Node statements : n.f8.nodes) {
        statements.accept(this, argu);
      }
      this.Outdent();

      // generate the vapor code for return expression
      this.Indent();
      String returnExpression = n.f10.accept(this, argu);
      log.info("ret: "+returnExpression);
      returnExpression = operandCheck(returnExpression);

      // check for implicit typecast
      log.info("Checking for implicit typecasting.");
      if (isClass(n.f1)) {
        log.info("Return type is a class.");
        log.info("Global type: "+globalType);
        log.info("Return type: "+return_type);
        LinkedList<SymTable> q = new LinkedList<>();
        q.add(symbolTable);
        if (!globalType.equals(return_type)) {
          log.info("Need to perform typecasting.");
          tmp.createNew();
          Binding b = null;
          while (!q.isEmpty()) {
            SymTable s = q.remove();
            if (s == null)
              continue;
            // search in the current function symbol table
            b = s.getBindingInformation(return_type);
            if (b != null) {
              if (b.getScope() != Binding.SCOPE.NULL) {
                addToVaporCode(tmp.get()+" = ["+returnExpression+"+"+b.getScope()+"]");
                returnExpression = tmp.get();
              } else {
                // this implies that the symbol is in the local function scope or in the function parameter list.
                Binding tmpBinding = rootSymTable.getSymTable(globalType).getBindingInformation(return_type);
                addToVaporCode(tmp.get()+" = ["+returnExpression+"+"+tmpBinding.getScope()+"]");
                returnExpression = tmp.get();
              }
              break;
            }
            // If i am at the global symbol table, and I have not found the identifier i am looking for, then ignore adding
            // the entries of the global symbol table as next candidate symbol tables to search for.
            if (s.getParent() == null) {
              break;
            }
            // if the symbol is not found in the current table, schedule its parent classes to be searched next
            SymTable parentClass = s.getParentClass();
            if (parentClass != null) {
              q.addFirst(parentClass);
              Binding tmpBinding = s.getBindingInformation(parentClass.getName());
              addToVaporCode(tmp.get()+" = ["+returnExpression+"+"+tmpBinding.getScope()+"]");
              returnExpression = tmp.get();
            }
            // Also schedule the parent symbol table to be searched next
            q.addLast(s.getParent());
          }
        }
      }

      this.addToVaporCode("ret " + returnExpression);
      this.Outdent();

      // move back to the root symbol table
      symbolTable = symbolTable.getParent();

      //
      tmp.reset();
      this.addToVaporCode("\n");
    } catch (Exception e) {
      log.printStackTrace(e);
    }
    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> FormalParameter()
   * f1 -> ( FormalParameterRest() )*
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(FormalParameterList n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    StringBuilder sb = new StringBuilder();

    // Enter the first parameter
    if (n.f0 != null) {
      sb.append(" ");
      sb.append(n.f0.accept(this, argu));
    }

    // Enter the Remaining parameters
    for (Node parameter : n.f1.nodes) {
      sb.append(" ");
      sb.append(parameter.accept(this, argu));
    }

    log.info("Left "+n.getClass().getSimpleName());
    return sb.toString();
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(FormalParameter n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    n.f0.accept(this, argu);
    String retVal = n.f1.f0.tokenImage;

    log.info("Left "+n.getClass().getSimpleName());
    return retVal;
  }

  /**
   * f0 -> ","
   * f1 -> FormalParameter()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(FormalParameterRest n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String retVal = n.f1.accept(this, argu);
    log.info("Left "+n.getClass().getSimpleName());
    return retVal;
  }

  /**
   * f0 -> ArrayType()
   * | BooleanType()
   * | IntegerType()
   * | Identifier()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(Type n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    if (n.f0.choice instanceof Identifier) {
      setGlobalType(((Identifier) n.f0.choice).f0.tokenImage);
    } else {
      n.f0.accept(this, argu);
    }

    log.info("Left "+n.getClass().getSimpleName());
    return n.getClass().getSimpleName();
  }

  /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(ArrayType n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    setGlobalType("int[]");

    log.info("Left "+n.getClass().getSimpleName());
    return n.getClass().getSimpleName();
  }

  /**
   * f0 -> "boolean"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(BooleanType n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    setGlobalType("boolean");

    log.info("Left "+n.getClass().getSimpleName());
    return n.getClass().getSimpleName();
  }

  /**
   * f0 -> "int"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(IntegerType n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    setGlobalType("int");

    log.info("Left "+n.getClass().getSimpleName());
    return n.getClass().getSimpleName();
  }

  /**
   * f0 -> Block()
   * | AssignmentStatement()
   * | ArrayAssignmentStatement()
   * | IfStatement()
   * | WhileStatement()
   * | PrintStatement()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(Statement n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String retval = n.f0.accept(this, argu);
    log.info("Left "+n.getClass().getSimpleName());
    return retval;
  }

  /**
   * f0 -> "{"
   * f1 -> ( Statement() )*
   * f2 -> "}"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(Block n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    // from the list of one or more statements, get one statement at a time
    for (Node statements : n.f1.nodes) {
      String statement = statements.accept(this, argu);
      log.info(""+statement);
    }

    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Expression()
   * f3 -> ";"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(AssignmentStatement n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String lhs = n.f0.accept(this, argu);
    String rhs = n.f2.accept(this, argu); // this should return the temp variable used

    log.info("LHS: " + lhs);
    log.info("RHS: " + rhs);

    // this is because the complete statement can be [this+8] = call t.0(t.1) which is
    // syntactically incorrect. The operandCheck() function will put the rhs in a separate
    // variable and assign the lhs to that variable.
    addToVaporCode(lhs + " = " + operandCheck(rhs) + "");

    setGlobalType("");

    log.info("Left " + n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> Identifier()
   * f1 -> "["
   * f2 -> Expression()
   * f3 -> "]"
   * f4 -> "="
   * f5 -> Expression()
   * f6 -> ";"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(ArrayAssignmentStatement n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

//    addToVaporCode("// Identifier(): "+n.getClass().getSimpleName());

    String exp = n.f0.accept(this, argu);
    log.info("OFFSET: "+OFFSET);
    tmp.createNew();
    addToVaporCode(tmp.get()+" = "+exp);
    addNullCheckToVaporCode();
    String lhsExp = tmp.get();

//    addToVaporCode("// [ Expression() ]");

    exp = n.f2.accept(this, argu);
    exp = operandCheck(exp);
    BOUNDS lbl = (BOUNDS) LabelFactory.getInstance().createLabel(LabelFactory.TYPE.BOUNDS);
    tmp.createNew();
    addToVaporCode(tmp.get()+" = ["+lhsExp+"]");
    addToVaporCode(tmp.get()+" = Lt("+exp+" "+tmp.get()+")");
    addToVaporCode("if "+tmp.get()+" goto :"+lbl.getBoundsLabel());
    addToVaporCode("  Error(\"array index out of bounds\")");
    addToVaporCode(lbl.getBoundsLabel()+":");
    addToVaporCode(String.format("%s = MulS(%s 4)", tmp.get(), exp));
    addToVaporCode(tmp.get()+" = Add("+tmp.get()+" "+lhsExp+")");
//    addToVaporCode(tmp.get()+" = Add("+tmp.get()+" "+tmp.get()+")");
    String arrIdx = tmp.get();


    exp = n.f5.accept(this, argu);
    exp = operandCheck(exp);
//    addToVaporCode("// Expression(): "+exp);
    if (OFFSET < 0) {
      if (exp.charAt(0) == '[') {
        tmp.createNew();
        addToVaporCode(tmp.get()+" = " + exp);
        addToVaporCode("[" + arrIdx + "+" + 4 + "] = " + tmp.get());

      } else {
        addToVaporCode("[" + arrIdx + "+" + 4 + "] = " + exp);
      }
    } else {
      addToVaporCode("[" + tmp.get() + "+" + OFFSET + "] = " + exp);
    }

    setGlobalType("");

    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> "if"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   * f5 -> "else"
   * f6 -> Statement()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(IfStatement n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    IF_ELSE lbl = (IF_ELSE) LabelFactory.getInstance().createLabel(LabelFactory.TYPE.IF_ELSE);

    String exp = n.f2.accept(this, argu); // returns last temporary used

    log.info("full Expression: "+exp);
    setGlobalType("");

    // Add the if statement to vapor code
    addToVaporCode(String.format("if0 %s goto :%s", operandCheck(exp), lbl.getIfElse()));

    // get all the if statements
    this.Indent();
    n.f4.accept(this, argu); // write all statements to finalVaporCode
    this.Outdent();

    // Add ifEnd statement
    this.Indent();
    this.addToVaporCode("goto :"+lbl.getIfEnd());
    this.Outdent();

    // Add ifElse label
    this.addToVaporCode(lbl.getIfElse()+":");

    // statements for else part of if statement
    this.Indent();
    n.f6.accept(this, argu); // write all statements to finalVaporCode
    this.Outdent();

    // Add ifEnd label
    this.addToVaporCode(lbl.getIfEnd()+":");
    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(WhileStatement n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    WHILE lbl = (WHILE) LabelFactory.getInstance().createLabel(LabelFactory.TYPE.WHILE);

    addToVaporCode(lbl.getwhileTest()+":");

    String exp = n.f2.accept(this, argu);
    exp = operandCheck(exp);
    log.info("Exp: "+exp);
    addToVaporCode("if0 "+exp+" goto :"+lbl.getwhileEnd());

    setGlobalType("");

    Indent();
    n.f4.accept(this, argu);
    addToVaporCode("goto :"+lbl.getwhileTest());
    Outdent();

    addToVaporCode(lbl.getwhileEnd()+":");

    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(PrintStatement n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String expr = n.f2.accept(this, argu);
    log.info("Expression: "+expr);

    if (expr.equals("this")) {
      tmp.createNew();
      addToVaporCode(tmp.get()+" = [this + "+OFFSET+"]");
      addToVaporCode("PrintIntS("+tmp.get()+")");
    } else {
      if (expr.chars().allMatch(Character::isDigit)) addToVaporCode("PrintIntS("+expr+")");
      else {
        tmp.createNew();
        addToVaporCode(tmp.get() + " = " + expr);
        addToVaporCode("PrintIntS(" + tmp.get() + ")");
      }
    }

    setGlobalType("");

    log.info("Left "+n.getClass().getSimpleName());
    return null;
  }

  /**
   * f0 -> AndExpression()
   * | CompareExpression()
   * | PlusExpression()
   * | MinusExpression()
   * | TimesExpression()
   * | ArrayLookup()
   * | ArrayLength()
   * | MessageSend()
   * | PrimaryExpression()
   *
   * <p>
   *
   * </p>
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(Expression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String retval = n.f0.accept(this, argu);
    log.info("Left "+n.getClass().getSimpleName());
    return retval;
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(AndExpression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    String lhs = n.f0.accept(this, argu);
    log.info("LHS: "+lhs);
    lhs = operandCheck(lhs);

    String rhs = n.f2.accept(this, argu);
    log.info("RHS: "+rhs);
    rhs = operandCheck(rhs);

    setGlobalType(Binding.TYPE.BOOLEAN);

    log.info("Left "+n.getClass().getSimpleName());
    return "MulS("+lhs+" "+rhs+")";
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(CompareExpression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    String rhs = n.f2.accept(this, argu);
    rhs = operandCheck(rhs);
    log.info("RHS: "+rhs);

    String lhs = n.f0.accept(this, argu);
    lhs = operandCheck(lhs);
    log.info("LHS: "+lhs);

    setGlobalType(Binding.TYPE.BOOLEAN);

    log.info("Left "+n.getClass().getSimpleName());
    return "LtS("+lhs+" "+rhs+")";
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(PlusExpression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String rhs = n.f2.accept(this, argu);
    rhs = operandCheck(rhs);
    log.info("RHS: "+rhs);

    String lhs = n.f0.accept(this, argu);
    lhs = operandCheck(lhs);
    log.info("LHS: "+lhs);

    setGlobalType(Binding.TYPE.INTEGER);

    log.info("Left "+n.getClass().getSimpleName());
    return "Add("+lhs+" "+rhs+")";
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(MinusExpression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String lhs = n.f0.accept(this, argu);
    log.info("LHS before op check: "+lhs);
    lhs = operandCheck(lhs);
    log.info("LHS: "+lhs);

    String rhs = n.f2.accept(this, argu);
    log.info("RHS: "+rhs);
    rhs = operandCheck(rhs);

    setGlobalType(Binding.TYPE.INTEGER);

    log.info("Left "+n.getClass().getSimpleName());
    return "Sub("+lhs+" "+rhs+")";
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(TimesExpression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    String lhs = n.f0.accept(this, argu);
    lhs = operandCheck(lhs);
    log.info("LHS: "+lhs);

    String rhs = n.f2.accept(this, argu);
    log.info("RHS: "+rhs);
    rhs = operandCheck(rhs);

    setGlobalType(Binding.TYPE.INTEGER);

    log.info("Left "+n.getClass().getSimpleName());
    return "MulS("+lhs+" "+rhs+")";
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(ArrayLookup n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    tmp.createNew();
    String exp = n.f0.accept(this, argu);
    addToVaporCode(tmp.get()+" = "+exp);
    addNullCheckToVaporCode();
    String pTmp = tmp.get();
    tmp.createNew();
    final String tmpBefore = tmp.get();
    addToVaporCode(tmp.get()+" = ["+pTmp+"]");

    BOUNDS lbl = (BOUNDS) LabelFactory.getInstance().createLabel(LabelFactory.TYPE.BOUNDS);
    String midExp = n.f2.accept(this, argu);
    int offset = 4;//(OFFSET==Binding.SCOPE.NULL)?4:OFFSET;

    // Error checking
    midExp = operandCheck(midExp); // creates a new temporary
    addToVaporCode(tmpBefore+" = Lt("+midExp+" "+tmpBefore+")");
    addToVaporCode("if "+tmpBefore+" goto :"+lbl.getBoundsLabel());
    addToVaporCode("  Error(\"array index out of bounds\")");
    addToVaporCode(lbl.getBoundsLabel()+":");

    addToVaporCode(tmpBefore+" = MulS("+midExp+" "+"4)");
    addToVaporCode(tmpBefore+" = Add("+tmpBefore+" "+pTmp+")");

    setGlobalType(globalType.substring(0, globalType.length()-2));

    log.info("Left "+n.getClass().getSimpleName());
    return "["+tmpBefore+"+"+offset+"]";
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(ArrayLength n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    String exp = n.f0.accept(this, argu);
    log.info("Exp: "+exp);

    tmp.createNew();
    addToVaporCode(tmp.get()+" = "+exp);
    String prevTempVar = tmp.get();

    tmp.createNew();
    addToVaporCode(tmp.get()+" = ["+prevTempVar+"]");

    setGlobalType(Binding.TYPE.INTEGER);

    log.info("Left "+n.getClass().getSimpleName());
    return tmp.get();
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   *
   * <p>
   *   Identifier() will always be a function
   * </p>
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(MessageSend n, String argu) {
    String retval = null;
    try {
      log.info("Entered " + n.getClass().getSimpleName());

      String primaryExpressionTemp = n.f0.accept(this, argu);
      String primaryExpressionType = globalType;
      log.info("PrimaryExpression: " + primaryExpressionTemp);

      String identifier = n.f2.f0.tokenImage + "()";
      setOFFSET(rootSymTable.getSymTable(globalType).lookup(identifier).getScope());
      if (OFFSET == Binding.SCOPE.NULL) {
        log.error("Cannot work with null binding or negative offsets");
        System.exit(-1);
      }
      log.info("OFFSET: " + OFFSET);

      log.info("primary expression before op check: " + primaryExpressionTemp);
      primaryExpressionTemp = operandCheck(primaryExpressionTemp);
      log.info("PrimaryExpression after operand check: " + primaryExpressionTemp);


      // Get the list of symbol tables containing the identifier
      LinkedList<SymTable> symbolTableList = GET_SYMBOL_TABLE_LIST(identifier);
      Binding binding = null;
      String _el = "";
      for (SymTable symTable : symbolTableList) {
        log.info("Grabbed Symbol table: " + symTable.getName());

        msgVars.FunctionExpressionStack().push(new Function() {{
          this.s = symTable;
          for (String s : this.s.getSymbolKeySet()) {
            Binding b = this.s.getBindingInformation(s);
            if (b == null || b.getScopeName() == Binding.SCOPE.NULL)
              continue;
            if (b.getScopeName() == Binding.SCOPE.PARAM)
              this.parameterCount++;
          }
          log.info("Number of parameters: " + this.parameterCount);
        }});

        final int originalFinalVaporCodeSize = finalVaporCode.size();
        final int tmpCounter = tmp.getCOUNTER();

        log.info("ExpressionList(): " + n.f4.node);
        if (n.f4.node != null) {
          _el = n.f4.accept(this, argu);
          log.info("Expression List: " + _el);
        }

        log.info("Number of parameters remaining: " + msgVars.FunctionExpressionStack().peek().parameterCount);
        if (msgVars.FunctionExpressionStack().peek().parameterCount == 0) {
          log.info("getting: " + symTable.getName() + " from parent: " + symTable.getParent().getName());
          binding = null;//symTable.getParent().getBindingInformation(symTable.getName());
          log.info("primaryExpressionType: "+primaryExpressionType);
          LinkedList<SymTable> q = new LinkedList<>();
          q.add(rootSymTable.getSymTable(primaryExpressionType));
          tmp.createNew();
          while (!q.isEmpty()) {
            SymTable s = q.remove();
            if (s == null)
              continue;
            // search in the current function symbol table
            log.info("looking for: "+symTable.getName()+" in "+s.getName());
            binding = s.getBindingInformation(symTable.getName());
            if (binding != null && s.getName().equals(symTable.getParent().getName())) {
              log.info("Found proper binding.");
              setOFFSET(binding.getScope());
              break;
            }
            // If i am at the global symbol table, and I have not found the identifier i am looking for, then ignore adding
            // the entries of the global symbol table as next candidate symbol tables to search for.
            if (s.getParent() == null) {
              break;
            }
            // if the symbol is not found in the current table, schedule its parent classes to be searched next
            SymTable parentClass = s.getParentClass();
            if (parentClass != null) {
              q.addFirst(parentClass);
              Binding tmpBinding = s.getBindingInformation(parentClass.getName());
              addToVaporCode(tmp.get()+" = ["+primaryExpressionTemp+"+"+tmpBinding.getScope()+"]");
              primaryExpressionTemp = tmp.get();
            }
            // Also schedule the parent symbol table to be searched next
            q.addLast(s.getParent());
          }
//          setOFFSET(binding.getScope());
          break;
        } else {
          log.error("Argument error.");
          log.error("restoring vapor code to original.");
          log.info("originalFinalVaporCode size: " + originalFinalVaporCodeSize);
          log.info("finalVaporCode size: " + finalVaporCode.size());
          while (finalVaporCode.size() != originalFinalVaporCodeSize)
            finalVaporCode.removeLast();
          tmp.setCOUNTER(tmpCounter);
          binding = null;
        }
        msgVars.FunctionExpressionStack().pop();
        setGlobalType(primaryExpressionType);
      }

      tmp.createNew();
      addToVaporCode(tmp.get() + " = [" + primaryExpressionTemp + "]");
      addToVaporCode(tmp.get() + " = [" + tmp.get() + "+" + OFFSET + "]");
      String methodTemporary = tmp.get();
      retval = "call " + methodTemporary + "(" + primaryExpressionTemp + _el + ")";

      // set the global type to the type of identifier()
      setGlobalType(((NodeToken) binding.getTypeObject().f0.choice).tokenImage);

      log.info("Left " + n.getClass().getSimpleName());
    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      System.exit(-1);
    }
    return retval;
  }

  /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )*
   *
   * Returns a linked list of expressions not assigned to anything. It is the job of the calling function to assign
   * these to temporaries.
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(ExpressionList n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    Function F = msgVars.FunctionExpressionStack().peek();
    SymTable symbolTablePointer = F.s;

    // Get a list of binding information of all the function parameters
    Iterator<Binding> parameterListIterator = symbolTablePointer.getParameterListIterator();
    Iterator<String> symbolIterator = symbolTablePointer.getSymbolKeySet().iterator();
    log.info("Number of params: "+symbolTablePointer.getParameterList().size());
    log.info("Function name: "+symbolTablePointer.getName());

    String exp = n.f0.accept(this, argu);
    exp = operandCheck(exp);
    exp = " "+exp;
    log.info("Expression: "+exp);

    F.decrementParameterCount();

    // check to make sure that the type of first expression type checks.
    Binding binding = parameterListIterator.hasNext() ? parameterListIterator.next() : null;
    log.info("Function name: "+symbolTablePointer.getName());
    log.info("Parameter: "+(symbolIterator.hasNext() ? symbolIterator.next() : null));
    if (binding == null) {
      log.error("Not the right function.");
      log.error("This function takes 0 args, but 1 was given.");
      setGlobalType("");
      return "";
    }


    String arg0type = binding.getStringType();
    log.info("Comparing argument type '" + arg0type + "' to '" + globalType+"'");
    if (!arg0type.equals(globalType)) {
      // if the two arguments do not match, check to see if one extends the other
      try {
        tryCasting(arg0type, globalType);
      } catch (TypeCheckException e) {
        log.printStackTrace(e);
        F.incrementParameterCount(); // spit out the consumed parameter
        log.error("Returning from function");
        setGlobalType("");
        return "";
      }
    }

    log.info("Parameters remaining: "+F.parameterCount);
    log.info("Visiting ExpressionRest()");
    StringBuilder sb = new StringBuilder();
    sb.append(exp);
    for (Node N : n.f1.nodes) {
      // Check to make sure that the function can take more arguments
      binding = parameterListIterator.hasNext() ? parameterListIterator.next() : null;
      log.info("Parameter: "+(symbolIterator.hasNext() ? symbolIterator.next() : null));
      if (binding == null) {
        log.error("More arguments passed to function than parameter list.");
        log.error(symbolTablePointer.getName());
        setGlobalType("");
        return "";
      }


      sb.append(" "+operandCheck(N.accept(this, argu)));
      log.info(""+binding.getScope());
      log.info("globalType: "+globalType);

      // Check to make sure that the argument types match
      log.info(""+binding);
      arg0type = binding.getStringType();
      if (!arg0type.equals(globalType)) {
        // if the two arguments do not match, check to see if one extends the other
        try {
          tryCasting(arg0type, globalType);
        } catch (TypeCheckException e) {
          log.printStackTrace(e);
          log.error("Argument type mismatch: "+arg0type+", "+globalType);
          setGlobalType("");
          return "";
        }
      }

      F.decrementParameterCount();
    }
    exp = sb.toString();


    // Make sure that we have consumed all the parameters
    binding = parameterListIterator.hasNext() ? parameterListIterator.next() : null;
    log.info("remaining binding information: " + binding);
    // if the binding information of the next parameter is not null then we have more parameters left
    if (binding != null) {
      log.info("Moving on to the next function.");
      setGlobalType("");
      return "";
    }

    log.info("Expression: "+exp);
    log.info("Left "+n.getClass().getSimpleName());
    return exp;
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(ExpressionRest n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String retVal = n.f1.accept(this, argu);
    log.info("Left "+n.getClass().getSimpleName());
    return retVal;
  }

  /**
   * f0 -> IntegerLiteral()
   * | TrueLiteral()
   * | FalseLiteral()
   * | Identifier()
   * | ThisExpression()
   * | ArrayAllocationExpression()
   * | AllocationExpression()
   * | NotExpression()
   * | BracketExpression()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(PrimaryExpression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    log.info(log.CYAN(argu));
    String retVal = n.f0.accept(this, argu);

    return retVal;
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(IntegerLiteral n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String retVal = n.f0.tokenImage;
    setGlobalType(Binding.TYPE.INTEGER);
    log.info("Left "+n.getClass().getSimpleName());
    return retVal;
  }

  /**
   * f0 -> "true"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(TrueLiteral n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String retVal = "1";
    setGlobalType(Binding.TYPE.BOOLEAN);
    log.info("Left "+n.getClass().getSimpleName());
    return retVal;
  }

  /**
   * f0 -> "false"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(FalseLiteral n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String retVal = "0";
    setGlobalType(Binding.TYPE.BOOLEAN);
    log.info("Left "+n.getClass().getSimpleName());
    return retVal;
  }

  /**
   * f0 -> <IDENTIFIER>
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(Identifier n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    if (argu == null) argu = n.f0.tokenImage;
    else argu = n.f0.tokenImage+argu;


    log.info("Current symtable: "+symbolTable.getName());
    log.info("Current Class: "+currentClassStack.peek().getName());
    String retVal = null;

    try {
      Binding binding = null; //symbolTable.lookup(argu);
      retVal = "this";
      LinkedList<SymTable> q = new LinkedList<>();
      q.add(symbolTable);
      tmp.createNew();
      while (!q.isEmpty()) {
        SymTable s = q.remove();
        if (s == null)
          continue;
        // search in the current function symbol table
        log.info("srgu: "+argu);
        log.info("current symbol table: "+s.getName());
        binding = s.getBindingInformation(argu);
        if (binding != null) {
          if (binding.getScope() == Binding.SCOPE.NULL) { // identifier in method symbol table
//            tmp.delete();
            log.info("Returning: "+n.f0.tokenImage);
            retVal = n.f0.tokenImage;
          } else {
            log.info("symbolTable.getParent().getName(): "+symbolTable.getParent().getName());
            log.info("Binding found in symbol table: "+s.getName());
            if (!symbolTable.getParent().getName().equals(s.getName())) {
//              addToVaporCode(tmp.get() + " = [" + retVal + "+" + binding.getScope() + "]");
              retVal = "[" + retVal + "+" + binding.getScope() + "]";
            } else  {
//              tmp.delete();
              retVal = "[this+"+binding.getScope()+"]";
            }
          }
          break;
        }
        // If i am at the global symbol table, and I have not found the identifier i am looking for, then ignore adding
        // the entries of the global symbol table as next candidate symbol tables to search for.
        if (s.getParent() == null) {
          break;
        }
        // if the symbol is not found in the current table, schedule its parent classes to be searched next
        SymTable parentClass = s.getParentClass();
        if (parentClass != null) {
          q.addFirst(parentClass);
          Binding tmpBinding = s.getBindingInformation(parentClass.getName());
          addToVaporCode(tmp.get()+" = ["+retVal+"+"+tmpBinding.getScope()+"]");
          retVal = tmp.get();
        }
        // Also schedule the parent symbol table to be searched next
        q.addLast(s.getParent());
      }
      tmp.delete();


      log.info("Current ID: "+n.f0.tokenImage);
      log.info("Scope: "+binding.getScope());

      setOFFSET(binding.getScope());

      // Set the current class to the type of identifier
      if (!ReservedKeywords.contains(binding.getStringType())) {
        currentClassStack.push(rootSymTable.getSymTable(binding.getStringType()));
      }

      // if the identifier has been declared, put its type in the global type
      setGlobalType(((NodeToken) binding.getTypeObject().f0.choice).tokenImage);

    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }

    log.info("Left "+n.getClass().getSimpleName());
    return retVal;
  }

  /**
   * f0 -> "this"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(ThisExpression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String retVal = n.f0.tokenImage;
    currentClassStack.push(currentClassStack.get(0));
    setGlobalType(currentClassStack.peek().getName());
    log.info("Left "+n.getClass().getSimpleName());
    return retVal;
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   *
   * <p>
   *   This is the only exception to expression types, in that it returns the rhs of the expression and not the
   *   temporary used to evaluate and store the expression.
   * </p>
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(ArrayAllocationExpression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    String exp = n.f3.accept(this, argu);
    log.info("Exp: "+exp);
    exp = operandCheck(exp);

    tmp.createNew();
    addToVaporCode(tmp.get()+" = call :AllocArray("+exp+")");

    setGlobalType(Binding.TYPE.INTEGER_ARRAY);

    log.info("Left "+n.getClass().getSimpleName());
    return tmp.get();
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(AllocationExpression n, String argu) {
    try {
      log.info("Entered "+n.getClass().getSimpleName());

      NULL lbl = (NULL) LabelFactory.getInstance().createLabel(LabelFactory.TYPE.NULL);

      String className = n.f1.f0.tokenImage;
      log.info("Current Class: "+className);

      // to be used later by identifier visitor
      currentClassStack.push(rootSymTable.getSymTable(className));

      tmp.createNew();

      // Allocate memory;
      int size = rootSymTable.getSymTable(className).getNumAttributes()*4+4;
      this.addToVaporCode(String.format("%s = HeapAllocZ(%d)", tmp.get(), size));

      // set the value of the allocated memory
      this.addToVaporCode(String.format("[%s] = :vmt_%s", tmp.get(), className));


      // set all parent classes
      log.info("Allocating space for parents.");
      Queue<SymTable> parents = new LinkedList<>();
      SymTable parent = null;
      SymTable prevParent = rootSymTable.getSymTable(className);
      if (rootSymTable.getSymTable(className).getParentClass() != null)
        parents.add(rootSymTable.getSymTable(className).getParentClass());
      final int COUNTER = tmp.getCOUNTER();
      while (!parents.isEmpty()) {
        parent = parents.remove();
        log.info("\t working with current parent: "+parent.getName());
        log.info("\t previous parent: "+prevParent.getName());
        size = rootSymTable.getSymTable(parent.getName()).getNumAttributes()*4+4;
        Binding b = rootSymTable.getSymTable(prevParent.getName()).getBindingInformation(parent.getName());
        tmp.createNew();
        addToVaporCode("");
        addToVaporCode(tmp.get()+" = HeapAllocZ("+size+")");
        addToVaporCode("["+tmp.getPrev()+"+"+b.getScope()+"] = "+tmp.get());
        addToVaporCode(tmp.get()+" = ["+tmp.getPrev()+"+"+b.getScope()+"]");
        addToVaporCode("["+tmp.get()+"] = :vmt_"+parent.getName());
        addToVaporCode("");
        if (parent.getParentClass() != null)
          parents.add(parent.getParentClass());
        prevParent = parent;
      }
      tmp.setCOUNTER(COUNTER);


      // Error check
      this.addToVaporCode(String.format("if %s goto :%s", tmp.get(), lbl.getNullLabel()));
      this.addToVaporCode("  Error(\"null pointer\")");
      this.addToVaporCode(lbl.getNullLabel()+":");

      setGlobalType(n.f1.f0.tokenImage);
      log.info("Left " + n.getClass().getSimpleName());

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
    }
    return tmp.get();
  }

  /**
   * f0 -> "!"
   * f1 -> Expression()
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(NotExpression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());

    String expr = n.f1.accept(this, argu);
    expr = operandCheck(expr);

    tmp.createNew();
    addToVaporCode(tmp.get()+" = Sub("+1+" "+expr+")");
    expr = tmp.get();

    setGlobalType(Binding.TYPE.BOOLEAN);
    log.info("Left "+n.getClass().getSimpleName());
    return expr;
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   *
   * @param n
   * @param argu
   */
  @Override
  public String visit(BracketExpression n, String argu) {
    log.info("Entered "+n.getClass().getSimpleName());
    String retVal = n.f1.accept(this, argu);
    log.info("Left "+n.getClass().getSimpleName());
    return retVal;
  }

  /**
   *
   */
  private void AllocArray() {
    addToVaporCode("func AllocArray(size)");
    addToVaporCode("  bytes = MulS(size 4)");
    addToVaporCode("  bytes = Add(bytes 4)");
    addToVaporCode("  v = HeapAllocZ(bytes)");
    addToVaporCode("  [v] = size");
    addToVaporCode("  ret v");
  }

  private void addNullCheckToVaporCode() {
    NULL lbl = (NULL) LabelFactory.getInstance().createLabel(LabelFactory.TYPE.NULL);
    addToVaporCode("if "+tmp.get()+" goto :"+lbl.getNullLabel());
    addToVaporCode("  Error(\"null pointer\")");
    addToVaporCode(lbl.getNullLabel()+":");
  }

  private String operandCheck(String lhs) {
    LinkedList<String> lastFunc = new LinkedList<String>(){{
      add("MulS(");
      add("LtS(");
      add("Add(");
      add("Sub(");
      add("MulS(");
      add("[this");
      add("call t.");
      add("[t.");
    }};
    for (String f : lastFunc) {
      if (lhs.length()>=f.length() && lhs.substring(0, f.length()).equals(f)) {
        tmp.createNew();
        addToVaporCode(tmp.get()+" = "+lhs);
        lhs = tmp.get();
      }
    }
    return lhs;
  }

  private LinkedList<SymTable> GET_SYMBOL_TABLE_LIST(String identifier) {
    log.info("Identifier: "+identifier);
    log.info("globalType: "+globalType);
    LinkedList<SymTable> retVal = new LinkedList<>();
    try {
      // setup the current class and its parent class to be searched
      LinkedList<String> keyset = new LinkedList<>(inheritanceLinks.get(globalType));
      keyset.addFirst(globalType);
      log.info("keyset: "+keyset);

      for (String s : keyset) { // search the current class and its parents for the function symbol table
        log.info("Working with: "+s);
        SymTable S = rootSymTable.getSymTable(s);
        log.info("Looking for: "+identifier+" in symbol table: "+S.getName());
        S = S.getChildren().get(identifier);
        log.info(""+S);
        if (S != null && !retVal.contains(S))
          retVal.add(S);
      }

      log.info(""+retVal);

      if (retVal.isEmpty()) {
        log.error("Cound not find identifier");
        System.exit(-1);
      }
    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      System.exit(-1);
    }
    log.info("GET_SYMBOL_TABLE_LIST: ");
    if (log.getLogStatus()) {
      for (SymTable S : retVal) {
        log.info("\t "+S.getName());
      }
    }
    return retVal;
  }

  /**
   * The following function will always try to cast @arg2 to @arg1 as in the expression: @arg1 = @arg2. It will throw
   * an exception if the cast fails, it will throw an {@link TypeCheckException}
   * @param arg1
   * @param arg2
   */
  public void tryCasting(String arg1, String arg2) throws TypeCheckException {
    log.info("Entered tryCasting() function.");
    log.info("Tyring to cast: '"+arg2+"' to '"+arg1+"'");
    SymTable s1 = rootSymTable.getSymTable(arg1);
    SymTable s2 = rootSymTable.getSymTable(arg2);

    if (log.getLogStatus())
      for (String s : inheritanceLinks.keySet()) {
        System.out.print(s+" -> ");
        System.out.println(inheritanceLinks.get(s));
      }

    if (s1 == null || ReservedKeywords.contains(s1.getName())) {
      log.error("Cannot cast reserved types");
      throw new TypeCheckException("Cannot cast reserved types");
    }

    if (s2 == null || ReservedKeywords.contains(s2.getName())) {
      log.error("Cannot cast reserved types");
      throw new TypeCheckException("Cannot cast reserved types");
    }

    LinkedList<String> s1keyset = new LinkedList<>(inheritanceLinks.get(s1.getName()));
    LinkedList<String> s2keyset = new LinkedList<>(inheritanceLinks.get(s2.getName()));

    if (s1keyset == null) { // s1 does not inherit any class
      s1keyset = new LinkedList<>();
      s1keyset.add(s1.getName());
    }

    if (s2keyset == null) { // s2 does not inherit any class
      s2keyset = new LinkedList<>();
      s2keyset.add(s2.getName());
    }

    log.info("s1 keyset: "+s1keyset);
    log.info("s2 keyset: "+s2keyset);

    for (String t1 : s1keyset) {
      s1 = rootSymTable.getSymTable(t1);
      for (String t2 : s2keyset) {
        s2 = rootSymTable.getSymTable(t2);

        // check to see if arg2 can be cast to arg1
        if (s2 != null)
          if (!s2.getName().equals(arg1))
            throw new TypeCheckException("Argument type mismatch: "+arg1+", "+arg2);
          else {
            log.info(arg2 + " has been implicitly type casted to " + arg1);
            log.info("Parent of "+arg1+": "+s1);
            log.info("Parent of "+arg2+": "+s2);
            log.info("Left tryCasting() function.");
            return;
          }
        else {
          log.error(arg2 + " cannot be type casted to anything.");
          throw new TypeCheckException(arg2+" cannot be type casted to anything.");
        }

      }
    }

    log.info("Left tryCasting() function.");
  }

}
