package TypeChecking.TypeChecking;

import TypeChecking.SymbolTableVisitor.BindingInformation;
import TypeChecking.SymbolTableVisitor.ReservedKeywords;
import TypeChecking.SymbolTableVisitor.SymTable;
import core.syntaxtree.*;
import core.util.LOGGER;
import core.visitor.DepthFirstVisitor;

import java.util.ArrayList;
import java.util.LinkedList;

public class TypeCheckVisitor extends DepthFirstVisitor {

  // for logging
  private static final transient LOGGER log = new LOGGER(TypeCheckVisitor.class.getSimpleName());

  private SymTable symbolTable;
  private final ArrayList<Function> tempSymbolTablePtr = new ArrayList<>();
  private boolean errorStatus = false;
  private final SymTable rootSymbolTable;
  private String globalType = "";

  static class Function {
    SymTable s;
    int parameterCount;

    void decrementParameterCount() {
      log.info("Decrementing parameter count for function: " + s.getName());
      parameterCount -= 1;
      log.info("Final remaining number of parameters: " + parameterCount);
    }

    void incrementParameterCount() {
      log.info("Incrementing parameter count for function: " + s.getName());
      parameterCount += 1;
      log.info("Final remaining number of parameters: " + parameterCount);
    }
  }

  /**
   *
   * @param s The symbol table of the minijava program
   */
  public TypeCheckVisitor(SymTable s) {
    this.symbolTable = s;
    this.rootSymbolTable = s;
    log.info("symbolTable: "+symbolTable);
  }

  /**
   * Get the error status from the type check core.visitor
   * @return the value stored in errorStatus
   */
  public boolean getErrorStatus() {
    return this.errorStatus;
  }

  /**
   * Set the error status to true
   */
  private void setErrorStatus() {
    this.errorStatus = true;
  }

  /**
   * Setter for the globalType
   * @param type
   */
  private void setGlobalType(String type) {
    int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
    globalType = type;
    log.info(String.format("Global Type set to: \"%s\"", globalType), line);
  }

  //
  // User-generated core.visitor methods below
  //

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  @Override
  public void visit(Goal n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    n.f0.accept(this);
    if (getErrorStatus()) return;
    n.f1.accept(this);
    if (getErrorStatus()) return;
    log.info("Left "+n.getClass().getSimpleName());
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
   */
  @Override
  public void visit(MainClass n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f14.accept(this);
      if (getErrorStatus()) return;

      log.info("Current symbol table: "+symbolTable.getName());
      symbolTable = symbolTable.getSymTable(n.f1.f0.tokenImage); // move to the main class symbol table
      log.info("Current symbol table: "+symbolTable.getName());
      symbolTable = symbolTable.getSymTable(n.f6.tokenImage+"()"); // move to the main method
      log.info("Current symbol table: "+symbolTable.getName());

      n.f15.accept(this);
      if (getErrorStatus()) return;

      symbolTable = symbolTable.getParent().getParent(); // move back to the global symbol table

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> ClassDeclaration()
   *       | ClassExtendsDeclaration()
   */
  @Override
  public void visit(TypeDeclaration n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    n.f0.accept(this);
    if (getErrorStatus()) return;
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  @Override
  public void visit(ClassDeclaration n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      symbolTable = symbolTable.getSymTable(n.f1.f0.tokenImage); // switch to the class scope
      n.f3.accept(this); // core.Typecheck the variable declarations
      if (getErrorStatus()) return;
      n.f4.accept(this); // typecheck the method declarations
      if (getErrorStatus()) return;

      symbolTable = symbolTable.getParent();

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "extends"
   * f3 -> Identifier()
   * f4 -> "{"
   * f5 -> ( VarDeclaration() )*
   * f6 -> ( MethodDeclaration() )*
   * f7 -> "}"
   */
  @Override
  public void visit(ClassExtendsDeclaration n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      symbolTable = symbolTable.getSymTable(n.f1.f0.tokenImage); // switch to class scope
      n.f5.accept(this);
      if (getErrorStatus()) return;
      n.f6.accept(this);
      if (getErrorStatus()) return;
      symbolTable = symbolTable.getParent();

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  @Override
  public void visit(VarDeclaration n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f0.accept(this); // visiting this node will also set the global type
      if (getErrorStatus()) return;

      // make sure that the identifier and type are not the same string sequence
      log.info("Making sure that the identifier and type are not the same string sequence.");
      String identifierName = n.f1.f0.tokenImage;
      if (globalType.equals(identifierName))
        throw new TypeCheckException("Identifier and type are the same name: "+identifierName+" "+globalType);
      log.info(log.GREEN("Identifier and string are not the same string sequence."));

      setGlobalType("");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
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
   */
  @Override
  public void visit(MethodDeclaration n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName()+": "+n.f2.f0.tokenImage);
    try {
      n.f1.accept(this); // check the return type to make sure that it has been declared
      if (getErrorStatus()) return;

      String returnTypeString = globalType; // get the return type of the function

      n.f4.accept(this); // visit parameter list
      if (getErrorStatus()) return;
      n.f7.accept(this); // visit the variable declarations
      if (getErrorStatus()) return;

      // switch to method level scope
      symbolTable = symbolTable.getSymTable(n.f2.f0.tokenImage+"()");

      n.f8.accept(this); // visit the statements
      if (getErrorStatus()) return;
      n.f10.accept(this); // evaluate the return type expression
      if (getErrorStatus()) return;

      // check to make sure that the return type expression evaluates to the return type specified in the function
      if (!returnTypeString.equals(globalType))
        throw new TypeCheckException("Return type of return statement does not match return type of function: "+
            globalType+", "+returnTypeString);

      // switch back to the class level scope
      symbolTable = symbolTable.getParent();

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> FormalParameter()
   * f1 -> ( FormalParameterRest() )*
   */
  @Override
  public void visit(FormalParameterList n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    n.f0.accept(this); // core.Typecheck the first formal parameter
    if (getErrorStatus()) return;
    n.f1.accept(this); // typecheck the remaining formal parameters
    if (getErrorStatus()) return;
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   */
  @Override
  public void visit(FormalParameter n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f0.accept(this); // check to make sure that type has been defined
      if (getErrorStatus()) return;

      if (globalType.equals(n.f1.f0.tokenImage))
        throw new TypeCheckException("Function argument Identifier and type cannot be the same: "+globalType+" "
            +n.f1.f0.tokenImage);

      setGlobalType("");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> ","
   * f1 -> FormalParameter()
   */
  @Override
  public void visit(FormalParameterRest n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    n.f1.accept(this);
    if (getErrorStatus()) return;
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> ArrayType()
   *       | BooleanType()
   *       | IntegerType()
   *       | Identifier()
   */
  @Override
  public void visit(Type n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      // If type is a new type check to make sure it is declared in global scope
      if (n.f0.choice instanceof Identifier) {
        String type = ((Identifier) n.f0.choice).f0.tokenImage;
        log.info(log.RED("New type: "+type));
        boolean found = false;
        for (String id : rootSymbolTable.getChildren().keySet()) {
          if (id.equals(type)) {
            found = true;
            break;
          }
        }

        if (!found) {
          log.error("Type "+type+" has not been declared previously.");
          throw new TypeCheckException("Type: " + type + " not found.");
        }

        log.info(log.GREEN(type+" has been found."));
        setGlobalType(((Identifier) n.f0.choice).f0.tokenImage);

      } else {
        n.f0.accept(this);
        if (getErrorStatus()) return;
      }

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   */
  @Override
  public void visit(ArrayType n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    setGlobalType("int[]");
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "boolean"
   */
  @Override
  public void visit(BooleanType n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    setGlobalType("boolean");
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "int"
   */
  @Override
  public void visit(IntegerType n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    setGlobalType("int");
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> Block()
   *       | AssignmentStatement()
   *       | ArrayAssignmentStatement()
   *       | IfStatement()
   *       | WhileStatement()
   *       | PrintStatement()
   */
  @Override
  public void visit(Statement n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    n.f0.accept(this);
    if (getErrorStatus()) return;
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "{"
   * f1 -> ( Statement() )*
   * f2 -> "}"
   */
  @Override
  public void visit(Block n) {
    if (getErrorStatus()) return;
    n.f1.accept(this);
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Expression()
   * f3 -> ";"
   */
  @Override
  public void visit(AssignmentStatement n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f0.accept(this); // make sure the identifier has been defined
      if (getErrorStatus()) return;
      String lhsTypeString = globalType; // Get the type of the LHS

      n.f2.accept(this); // get the type of expression
      if (getErrorStatus()) return;

      log.info("Type of LHS: "+lhsTypeString);
      log.info("Type of RHS: "+globalType);

      // The type of the RHS is stored in globalType
      if (!lhsTypeString.equals(globalType)) {
        tryCasting(lhsTypeString, globalType);
      }

      setGlobalType("");
      log.info(n.getClass().getSimpleName()+" type checks.");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> Identifier()
   * f1 -> "["
   * f2 -> Expression()
   * f3 -> "]"
   * f4 -> "="
   * f5 -> Expression()
   * f6 -> ";"
   */
  @Override
  public void visit(ArrayAssignmentStatement n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f0.accept(this); // check to make sure that the identifier exists
      if (getErrorStatus()) return;
      n.f2.accept(this);
      if (getErrorStatus()) return;

      // Check the type of Expression() in between []
      if (!globalType.equals("int"))
        throw new TypeCheckException("Need array index expression to evaluate to int.");

      // Get the type of LHS
      BindingInformation binding = symbolTable.lookup(n.f0.f0.tokenImage);
      String lhsType = ((NodeToken)binding.getType().f0.choice).tokenImage;
      if (lhsType.equals("int[]")) lhsType = "int";

      log.info("Type of LHS: "+lhsType);

      n.f5.accept(this);
      if (getErrorStatus()) return;

      log.info("Type of RHS: "+globalType);

      if (!globalType.equals(lhsType))
        throw new TypeCheckException("Type mismatch.");

      log.info("Array assignment statement typechecks.");
      setGlobalType("");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "if"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   * f5 -> "else"
   * f6 -> Statement()
   */
  @Override
  public void visit(IfStatement n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f2.accept(this);
      if (getErrorStatus()) return;

      log.info("Expression type: "+globalType);
      if (!globalType.equals("boolean"))
        throw new TypeCheckException("if (Expression) does not typecheck to boolean");

      setGlobalType("");

      n.f4.accept(this);
      if (getErrorStatus()) return;

      log.info("Global type: "+globalType);
      if (!globalType.equals(""))
        throw new TypeCheckException("Expecting globalType to be empty if all statements type checked.");

      n.f6.accept(this);
      if (getErrorStatus()) return;

      if (!globalType.equals(""))
        throw new TypeCheckException("Expecting globalType to be empty if all statements type checked.");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   */
  @Override
  public void visit(WhileStatement n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f2.accept(this);
      if (getErrorStatus()) return;
      log.info("Expression type: "+globalType);
      if (!globalType.equals("boolean"))
        throw new TypeCheckException("while (Expression) does not typecheck to boolean");

      setGlobalType("");

      n.f4.accept(this);
      if (getErrorStatus()) return;
      if (!globalType.equals(""))
        throw new TypeCheckException("Expecting globalType to be empty if all statements type checked.");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   */
  @Override
  public void visit(PrintStatement n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f2.accept(this);
      if (getErrorStatus()) return;

      if (!globalType.equals("int"))
        throw new TypeCheckException("print statement can only print integers.");

      log.info(n.getClass().getSimpleName()+" type checks.");
      setGlobalType("");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> AndExpression()
   *       | CompareExpression()
   *       | PlusExpression()
   *       | MinusExpression()
   *       | TimesExpression()
   *       | ArrayLookup()
   *       | ArrayLength()
   *       | MessageSend()
   *       | PrimaryExpression()
   */
  @Override
  public void visit(Expression n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    n.f0.accept(this);
    if (getErrorStatus()) return;
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   */
  @Override
  public void visit(AndExpression n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f0.accept(this);
      if (getErrorStatus()) return;

      String lhsType = globalType;
      if (!lhsType.equals("boolean"))
        throw new TypeCheckException("LHS did not evaluate to boolean, but got "+lhsType);

      n.f2.accept(this);
      if (getErrorStatus()) return;

      if (!globalType.equals("boolean"))
        throw new TypeCheckException("RHS did not evaluate to boolean, but got "+globalType);

      setGlobalType("boolean");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  @Override
  public void visit(CompareExpression n) { // this needs to typecheck to boolean
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f0.accept(this);
      if (getErrorStatus()) return;

      log.info("LHS of compare expression: "+globalType);
      if (!globalType.equals("int"))
        throw new TypeCheckException("LHS of compare expression should be an integer.");

      n.f2.accept(this);
      if (getErrorStatus()) return;

      log.info("RHS of compare expression: "+globalType);
      if (!globalType.equals("int"))
        throw new TypeCheckException("RHS of compare expression should be an integer.");
      log.info(n.getClass().getSimpleName()+" typechecks.");

      setGlobalType("boolean"); // set the return type of the compare expression

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */
  @Override
  public void visit(PlusExpression n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f0.accept(this);
      if (getErrorStatus()) return;

      log.info(String.format("LHS of %s: %s", n.getClass().getSimpleName(), globalType));
      if (!globalType.equals("int"))
        throw new TypeCheckException(String.format("LHS of %s should be an integer.", n.getClass().getSimpleName()));

      n.f1.accept(this);
      if (getErrorStatus()) return;

      log.info(String.format("LHS of %s: %s", n.getClass().getSimpleName(), globalType));
      if (!globalType.equals("int"))
        throw new TypeCheckException(String.format("LHS of %s should be an integer.", n.getClass().getSimpleName()));

      setGlobalType("int");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  @Override
  public void visit(MinusExpression n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f0.accept(this);
      if (getErrorStatus()) return;

      log.info(String.format("LHS of %s: %s", n.getClass().getSimpleName(), globalType));
      if (!globalType.equals("int"))
        throw new TypeCheckException(String.format("LHS of %s should be an integer.", n.getClass().getSimpleName()));

      n.f1.accept(this);
      if (getErrorStatus()) return;

      log.info(String.format("RHS of %s: %s", n.getClass().getSimpleName(), globalType));
      if (!globalType.equals("int"))
        throw new TypeCheckException(String.format("LHS of %s should be an integer.", n.getClass().getSimpleName()));

      setGlobalType("int");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  @Override
  public void visit(TimesExpression n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f0.accept(this);
      if (getErrorStatus()) return;

      log.info(String.format("LHS of %s: %s", n.getClass().getSimpleName(), globalType));
      if (!globalType.equals("int"))
        throw new TypeCheckException(String.format("LHS of %s should be an integer.", n.getClass().getSimpleName()));

      n.f1.accept(this);
      if (getErrorStatus()) return;

      log.info(String.format("LHS of %s: %s", n.getClass().getSimpleName(), globalType));
      if (!globalType.equals("int"))
        throw new TypeCheckException(String.format("LHS of %s should be an integer.", n.getClass().getSimpleName()));

      setGlobalType("int");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */
  @Override
  public void visit(ArrayLookup n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f2.accept(this);
      if (getErrorStatus()) return;
      if (!globalType.equals("int"))
        throw new TypeCheckException("Array lookup expression needs to type check to int.");

      n.f0.accept(this);
      if (getErrorStatus()) return;
      if (!globalType.equals("int[]")) // TODO: are integer arrays the only type of arrays allowed?
        throw new TypeCheckException("primary expression does not evaluate to an array type.");
      setGlobalType(globalType.substring(0, globalType.length()-2));

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  @Override
  public void visit(ArrayLength n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      log.info("Visiting PrimaryExpression()");
      n.f0.accept(this);
      if (getErrorStatus()) return;
      log.info("Returned from PrimaryExpression()");

      if (!globalType.equals("int[]")) {
        log.error(n.getClass().getSimpleName()+" primary expression should be int[].");
        throw new TypeCheckException(n.getClass().getSimpleName()+" primary expression should be int[].");
      }

      log.info("LHS Type: "+globalType);

      setGlobalType("int");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   */
  @Override
  public void visit(MessageSend n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      log.info("Current symbol table pointer: "+symbolTable.getName());
      n.f0.accept(this);
      if (getErrorStatus()) return;
      log.info(log.PURPLE("PrimaryExpression returned type: "+globalType));

      // search the symbol tables for the identifier, and get its binding information
      String identifier = n.f2.f0.tokenImage+"()";
      log.info("looking up "+identifier);

      // Search the root symbol table and its scope for the identifier
      SymTable primaryExpressionSymbolTable = rootSymbolTable.getSymTable(globalType);
      if (primaryExpressionSymbolTable == null) {
        throw new TypeCheckException("Could not find primary expression in symbol table.");
      }
      log.info("Primary Expression: "+primaryExpressionSymbolTable.getName());

      LinkedList<SymTable> symTableList = primaryExpressionSymbolTable.getSymTableList(identifier);
      log.info("Num Identifiers: "+symTableList.size());
      if (symTableList.isEmpty()) {
        throw new TypeCheckException("Cannot find identifier: "+n.f2.f0.tokenImage+"()");
      }
      BindingInformation binding = null;

      while (!symTableList.isEmpty()) {
        SymTable symTable = symTableList.remove();
        if (log.getLogStatus()) {
          log.info("Parent: "+symTable.getParent().getName());
          symTable.print();
        }

        // move to the identifier scope
        tempSymbolTablePtr.add(
            new Function() {{
              this.s = symTable;
              this.parameterCount = this.s.getNumPrams();
              log.info("Number of parameters: " + this.parameterCount);
            }}
        );
        if (tempSymbolTablePtr.get(tempSymbolTablePtr.size() - 1).s == null)
          throw new TypeCheckException("Cannot find class: " + globalType);
        n.f4.accept(this); // typecheck the expression lis

        // check if all the parameters have been consumed
        log.info("Number of parameters remaining: " + tempSymbolTablePtr.get(tempSymbolTablePtr.size() - 1).parameterCount);
        if (tempSymbolTablePtr.get(tempSymbolTablePtr.size() - 1).parameterCount == 0 && !errorStatus) {
          binding = symTable.getParent().getBindingInformation(symTable.getName());
          symTableList.clear();
        } else {
          log.error("Argument error.");
          binding = null;
          errorStatus = false;
        }

        tempSymbolTablePtr.remove(tempSymbolTablePtr.size() - 1); // remove last element
      }

      if (binding == null) {
        throw new TypeCheckException("Identifier "+identifier+" with the correct number of parameters not found");
      }

      // set the global type to the type of identifier()
      setGlobalType(((NodeToken)binding.getType().f0.choice).tokenImage);

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )*
   */
  @Override
  public void visit(ExpressionList n) {
    if (getErrorStatus()) return;
    log.info("Entered " + n.getClass().getSimpleName());
    Function F = tempSymbolTablePtr.get(tempSymbolTablePtr.size() - 1);
    SymTable symbolTablePointer = F.s;

    // at this point the symbol table should point to the function symbol table
    log.info(log.CYAN("Symbol table pointing to: " + symbolTablePointer.getName()));
    log.info(log.CYAN("Global Type: " + globalType));

    // Get a list of binding information of all the function parameters
    ArrayList<BindingInformation> types = symbolTablePointer.getParametersList();
    log.info("Number of params: " + types.size());

    log.info("Visiting Expression().");
    n.f0.accept(this);
    if (getErrorStatus()) return;
    log.info("Returned from Expression()");
    log.info("Global type: " + globalType);

    log.info("Symbol table pointing to: " + symbolTablePointer);
    F.decrementParameterCount();

    // check to make sure that the type of first expression type checks.
    BindingInformation binding = symbolTablePointer.getNextBindingInformation();
    if (binding == null) {
      log.error("Not the right function.");
      log.error("This function takes 0 args, but 1 was given.");
      return;
    }
    String arg0type = ((NodeToken) binding.getType().f0.choice).tokenImage;
    log.info("Comparing argument type " + arg0type + " to " + globalType);
    if (!arg0type.equals(globalType)) {
      // if the two arguments do not match, check to see if one extends the other
      try {
        tryCasting(arg0type, globalType);
      } catch (TypeCheckException e) {
        log.printStackTrace(e);
        setErrorStatus();
        F.incrementParameterCount(); // spit out the consumed parameter
        log.error("Returning from function");
        return;
      }
    }

    log.info("Visiting ExpressionRest()");
    n.f1.accept(this);
    if (getErrorStatus()) return;
    log.info("Returned from ExpressionRest()");

    // Make sure that we have consumed all the parameters
    binding = symbolTablePointer.getNextBindingInformation();
    log.info("remaining binding information: " + binding);
    if (binding != null) {
      log.info("Moving on to the next function.");
      return;
//        throw new TypeCheckException("Mismatch in the number of arguments.");
    }

    // Reset the iterator counter
    symbolTablePointer.resetBindingInformationCounter();

    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  @Override
  public void visit(ExpressionRest n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      Function F = tempSymbolTablePtr.get(tempSymbolTablePtr.size()-1);
      SymTable symbolTablePointer = F.s;
      log.info(log.CYAN("Symbol table pointing to: "+symbolTablePointer.getName()));

      // Check to make sure that the function can take more arguments
      BindingInformation binding = symbolTablePointer.getNextBindingInformation();
      log.info("");
      if (binding == null)
        throw new TypeCheckException("More arguments passed to function than parameter list.");

      log.info("Visiting Expression()");
      n.f1.accept(this);
      if (getErrorStatus()) return;
      log.info("Returned from Expression()");

      // Check to make sure that the argument types match
      String argType = ((NodeToken)binding.getType().f0.choice).tokenImage;
      if (!argType.equals(globalType))
        throw new TypeCheckException("Argument type mismatch: "+argType+", "+globalType);

      F.decrementParameterCount();

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> IntegerLiteral()
   *       | TrueLiteral()
   *       | FalseLiteral()
   *       | Identifier()
   *       | ThisExpression()
   *       | ArrayAllocationExpression()
   *       | AllocationExpression()
   *       | NotExpression()
   *       | BracketExpression()
   */
  @Override
  public void visit(PrimaryExpression n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    n.f0.accept(this);
    if (getErrorStatus()) return;
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  @Override
  public void visit(IntegerLiteral n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    setGlobalType("int");
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "true"
   */
  @Override
  public void visit(TrueLiteral n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    setGlobalType("boolean");
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "false"
   */
  @Override
  public void visit(FalseLiteral n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    setGlobalType("boolean");
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  @Override
  public void visit(Identifier n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName()+": "+n.f0.tokenImage);
    try {
      log.info("Symbol table pointer: "+symbolTable);
      log.info("symbolTable pointing to: "+symbolTable.getName());

      // check to make sure that the identifier has been declared
      BindingInformation binding = symbolTable.lookup(n.f0.tokenImage);
      if (binding == null)
        throw new TypeCheckException("Identifer "+n.f0.tokenImage+" not found.");

      // if the identifier has been declared, put its type in the global type
      setGlobalType(((NodeToken) binding.getType().f0.choice).tokenImage);

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "this"
   */
  @Override
  public void visit(ThisExpression n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName()+": "+n.f0.tokenImage);
    // Get the type of keyword: this
    SymTable s;
    for (s = symbolTable; s.getParent().getParent() != null; s = s.getParent()) ;

    setGlobalType(s.getName());
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   */
  @Override
  public void visit(ArrayAllocationExpression n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f3.accept(this);
      if (getErrorStatus()) return;

      // check to make sure that global type is an "int"
      if (!globalType.equals("int"))
        throw new TypeCheckException("Cannot allocate non integer array sizes.");

      setGlobalType("int[]");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  @Override
  public void visit(AllocationExpression n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      // check to make sure identifier exists in the global scope
      if (!rootSymbolTable.getChildren().containsKey(n.f1.f0.tokenImage)) {
        throw new TypeCheckException("Type "+n.f1.f0.tokenImage+" not found.");
      }

      setGlobalType(n.f1.f0.tokenImage);

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "!"
   * f1 -> Expression()
   */
  @Override
  public void visit(NotExpression n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      n.f1.accept(this);
      if (getErrorStatus()) return;

      // check to make sure that expression evaluate to boolean
      if (!globalType.equals("boolean"))
        throw new TypeCheckException("Input type to "+n.getClass().getSimpleName()+" was not boolean.");
      log.info(n.getClass().getSimpleName()+" typechecked successfully.");

      setGlobalType("boolean");

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  @Override
  public void visit(BracketExpression n) { // we do not care what this evaluates to
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    n.f1.accept(this);
    if (getErrorStatus()) return;
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * The following function will always try to cast @arg2 to @arg1 as in the expression: @arg1 = @arg2. It will throw
   * an exception if the cast fails, it will throw an {@link TypeCheckException}
   * @param arg1
   * @param arg2
   */
  public void tryCasting(String arg1, String arg2) throws TypeCheckException {
    log.info("Entered tryCasting() function.");
    SymTable s1 = rootSymbolTable.getSymTable(arg1);
    SymTable s2 = rootSymbolTable.getSymTable(arg2);

    if (s1 == null || ReservedKeywords.contains(s1.getName())) {
      log.error("Cannot cast reserved types");
      throw new TypeCheckException("Cannot cast reserved types");
    }

    if (s2 == null || ReservedKeywords.contains(s2.getName())) {
      log.error("Cannot cast reserved types");
      throw new TypeCheckException("Cannot cast reserved types");
    }

    // Get the parent classes of arg1 and arg2
    s1 = s1.getParentClass();
    s2 = s2.getParentClass();

    log.info("Parent of "+arg1+": "+s1);
    log.info("Parent of "+arg2+": "+s2);

    if (s1 == null && s2 == null)
      throw new TypeCheckException("Argument type mismatch: "+arg1+", "+arg2);

    // check to see if arg2 can be cast to arg1
    if (s2 != null)
      if (!s2.getName().equals(arg1))
        throw new TypeCheckException("Argument type mismatch: "+arg1+", "+arg2);
      else
        log.info(arg2+" has been implicitly type casted to "+arg1);
    else {
      log.error(arg2 + " cannot be type casted to anything.");
      throw new TypeCheckException(arg2+" cannot be type casted to anything.");
    }

    log.info("Left tryCasting() function.");
  }
}
