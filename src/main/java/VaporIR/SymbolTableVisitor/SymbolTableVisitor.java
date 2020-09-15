package VaporIR.SymbolTableVisitor;

import TypeChecking.TypeChecking.TypeCheckException;
import core.syntaxtree.*;
import core.util.LOGGER;
import core.visitor.Visitor;

import java.beans.BeanInfo;
import java.util.Enumeration;

/**
 * This visitor class updates the variable types in method and variable declarations. This class also creates new
 * symbol tables for methods and nested statements - if statements and while loops and variables that are initialized
 * in scopes. This visitor class also generates the method symbol table for each class symbol table in the global
 * symbol table. This class also does a very trivial form of type checking
 */

public class SymbolTableVisitor implements Visitor {

  // for logging
  private static final transient LOGGER log = new LOGGER(SymbolTableVisitor.class.getSimpleName(), false);

  private SymTable globalSymbolTable = null;
  private boolean errorStatus = false;

  public SymbolTableVisitor() {
    this.globalSymbolTable = new SymTable(null, "GLOBAL");
  }

  /**
   *
   * @return
   */
  public boolean getErrorStatus() {
    return this.errorStatus;
  }

  /**
   *
   * @return
   */
  public SymTable getSymbolTable() {
    return this.globalSymbolTable;
  }

  /**
   *
   */
  private void setErrorStatus() {
    this.errorStatus = true;
  }
  
  @Override
  public void visit(NodeList n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());

    for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this);
      if (getErrorStatus()) return;
    }

    log.info("Leaving "+n.getClass().getSimpleName());
  }

  @Override
  public void visit(NodeListOptional n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());

    if (n.present()) {
      for (Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
        e.nextElement().accept(this);
        if (getErrorStatus()) return;
      }
    }

    log.info("Leaving "+n.getClass().getSimpleName());
  }

  @Override
  public void visit(NodeOptional n) {
    if (getErrorStatus()) return;
    if ( n.present() ) {
      n.node.accept(this);
      if (getErrorStatus()) return;
    }
  }

  @Override
  public void visit(NodeSequence n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());

    for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
      e.nextElement().accept(this);
      if (getErrorStatus()) return;
    }

    log.info("Leaving "+n.getClass().getSimpleName());
  }

  @Override
  public void visit(NodeToken n) { }

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  @Override
  public void visit(Goal n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.f0.getClass().getSimpleName());
    n.f0.accept(this); // visit main class
    if (getErrorStatus()) return;
    n.f1.accept(this); // visit other classes
    if (getErrorStatus()) return;
    log.info("Leaving from "+n.f1.getClass().getSimpleName());
    log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));
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
      log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));
      n.f1.accept(this);
      if (getErrorStatus()) return;

      // Add the binding information of the currently inserted symbol
      Type t = new Type( new NodeChoice( new NodeToken( n.f0.tokenImage ) ) );
      Binding binding = new Binding( t );
      globalSymbolTable.insert(globalSymbolTable.getLastInsertedSymbol(), binding);

      // advance to the last inserted symbol table, at this point this should be the main class symbol table
      globalSymbolTable = globalSymbolTable.getSymTable(globalSymbolTable.getLastInsertedSymbol());
      log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));

      // put the main method along with its binding in the main class symbol table.
      String identifier = "main()";
      SymTable symTable = new SymTable(globalSymbolTable, identifier);
      globalSymbolTable.insert(identifier, symTable);
      t = new Type( new NodeChoice( new NodeToken( n.f5.tokenImage ) ) );
      binding = new Binding( t );
      globalSymbolTable.insert(identifier, binding);
      log.info(log.RED("Added symbol table: "+identifier));

      // move to the main method symbol table
      globalSymbolTable = globalSymbolTable.getSymTable(globalSymbolTable.getLastInsertedSymbol());
      log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));

      n.f14.accept(this);// visit the variable declaration
      if (getErrorStatus()) return;
      n.f15.accept(this); // visit the statements
      if (getErrorStatus()) return;

      // move back to the class symbol table
      globalSymbolTable = globalSymbolTable.getParent();
      log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));

      // go back to the global symbol table
      globalSymbolTable = globalSymbolTable.getParent();
      log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();

    } catch (Exception e) {
      log.printStackTrace(e);
      System.exit(-1);
    }
    log.info("Leaving "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> ClassDeclaration()
   *       | ClassExtendsDeclaration()
   */
  @Override
  public void visit(TypeDeclaration n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    n.f0.accept(this);  // visit the class declaration and class extends declaration
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
      n.f1.accept(this);
      if (getErrorStatus()) return;

      // put the binding of the new class to the global symbol table
      Type t = new Type( new NodeChoice( new NodeToken( n.f0.tokenImage ) ) );
      Binding binding = new Binding( t );
      globalSymbolTable.insert(globalSymbolTable.getLastInsertedSymbol(), binding);

      // advance the symbol table pointer to now point to the newly added class symbol table
      globalSymbolTable = globalSymbolTable.getSymTable(globalSymbolTable.getLastInsertedSymbol());
      log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));

      n.f3.accept(this); // visit the variable declarations
      if (getErrorStatus()) return;
      n.f4.accept(this); // visit the method declarations
      if (getErrorStatus()) return;

      // move back to the global symbol table
      globalSymbolTable = globalSymbolTable.getParent();
      log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();

    } catch (Exception e) {
      log.printStackTrace(e);
      System.exit(-1);
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  // TODO: Complete this.
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
      n.f1.accept(this); // visit identifier
      if (getErrorStatus()) return;

      // put the binding of the new class to the global symbol table
      Type t = new Type( new NodeChoice( new NodeToken( n.f0.tokenImage ) ) );
      Binding binding = new Binding( t );
      globalSymbolTable.insert(globalSymbolTable.getLastInsertedSymbol(), binding);

      // advance the symbol table pointer to now point to the newly added class symbol table
      globalSymbolTable = globalSymbolTable.getSymTable(globalSymbolTable.getLastInsertedSymbol());
      log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));

      n.f5.accept(this); // visit the variable declarations
      if (getErrorStatus()) return;
      n.f6.accept(this); // visit the method declaration
      if (getErrorStatus()) return;

      // Move back to the root of the hierarchical symbol table
      globalSymbolTable = globalSymbolTable.getParent();

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();

    } catch (Exception e) {
      log.printStackTrace(e);
      System.exit(-1);
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
    n.f1.accept(this); // visit the identifier of the variable
    if (getErrorStatus()) return;
    n.f0.accept(this); // visit the type of the variable
    if (getErrorStatus()) return;
    log.info("Leaving "+n.getClass().getSimpleName());
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
    log.info("Entered "+n.getClass().getSimpleName());
    try {
//      n.f2.accept(this); // visit the identifier
      if (getErrorStatus()) return;
      String identifier = n.f2.f0.tokenImage+"()";
      SymTable symTable = new SymTable(globalSymbolTable, identifier);
      globalSymbolTable.insert(identifier, symTable);

      n.f1.accept(this); // visit the type
      if (getErrorStatus()) return;


      // move to the newly declared method symbol table
      globalSymbolTable = globalSymbolTable.getSymTable(globalSymbolTable.getLastInsertedSymbol());
      log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));

      n.f4.accept(this); // visit the parameter list
      if (getErrorStatus()) return;
      n.f7.accept(this); // visit the variable declaration list
      if (getErrorStatus()) return;

      // move back to the class symbol table
      globalSymbolTable = globalSymbolTable.getParent();
      log.info(log.RED("Current Symbol table: "+globalSymbolTable.getName()));

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();

    } catch (Exception e) {
      log.printStackTrace(e);
      System.exit(-1);
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
    n.f0.accept(this); // visit the first parameter
    if (getErrorStatus()) return;
    n.f1.accept(this); // visit the remaining parameters
    if (getErrorStatus()) return;
    log.info("left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   */
  @Override
  public void visit(FormalParameter n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    n.f1.accept(this); // visit the first parameter
    if (getErrorStatus()) return;
    n.f0.accept(this); // visit the type of the first parameter
    if (getErrorStatus()) return;

    // set the scope of the last inserted function parameter
    globalSymbolTable.getBindingInformation(globalSymbolTable.getLastInsertedSymbol()).setScope(Binding.SCOPE.PARAM);
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
      // If the type is an identifier, do not create a new symbol table for it
      if (n.f0.choice instanceof Identifier) {
        log.info(log.RED("Type is an Identifier."));
        Type t = new Type( new NodeChoice( new NodeToken( ((Identifier)n.f0.choice).f0.tokenImage ) ) );
        Binding binding = new Binding( t );
        globalSymbolTable.insert(globalSymbolTable.getLastInsertedSymbol(), binding);

      } else {
        n.f0.accept(this); // visit the actual type of the variable
        if (getErrorStatus()) return;
      }

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();

    } catch (Exception e) {
      log.printStackTrace(e);
      System.exit(-1);
    }
    log.info("Leaving "+n.getClass().getSimpleName());
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
    try {
      // Insert the binding information of the last inserted symbol into the current symbol table.
      String typeString = "int[]";
      Type t = new Type( new NodeChoice( new NodeToken( typeString ) ) );
      Binding binding = new Binding( t );
      globalSymbolTable.insert(globalSymbolTable.getLastInsertedSymbol(), binding);

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();

    } catch (Exception e) {
      log.printStackTrace(e);
      System.exit(-1);
    }
    log.info("Leaving "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "boolean"
   */
  @Override
  public void visit(BooleanType n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      // Insert the binding information of the last inserted symbol into the current symbol table.
      Type t = new Type( new NodeChoice( new NodeToken( n.f0.tokenImage ) ) );
      Binding binding = new Binding( t );
      globalSymbolTable.insert(globalSymbolTable.getLastInsertedSymbol(), binding);

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();

    } catch (Exception e) {
      log.printStackTrace(e);
      System.exit(-1);
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> "int"
   */
  @Override
  public void visit(IntegerType n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      // Insert the binding information of the last inserted symbol into the current symbol table.
      Type t = new Type( new NodeChoice( new NodeToken( n.f0.tokenImage ) ) );
      Binding binding = new Binding( t );
      globalSymbolTable.insert(globalSymbolTable.getLastInsertedSymbol(), binding);

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();

    } catch (Exception e) {
      log.printStackTrace(e);
      System.exit(-1);
    }
    log.info("Leaving "+n.getClass().getSimpleName());
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
    n.f0.accept(this); // visit each of the statements mentioned in the block comment before this function
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
    log.info("Entered "+n.getClass().getSimpleName());
    n.f1.accept(this);
    if (getErrorStatus()) return;
    log.info("Left "+n.getClass().getSimpleName());
  }

  //----------------------------------------------------------------------------
  // We do not care about expressions when building the symbol table
  //----------------------------------------------------------------------------

  @Override
  public void visit(AssignmentStatement n) { }

  @Override
  public void visit(ArrayAssignmentStatement n) { }

  @Override
  public void visit(IfStatement n) { }

  @Override
  public void visit(WhileStatement n) { }

  @Override
  public void visit(PrintStatement n) { }

  @Override
  public void visit(Expression n) { }

  @Override
  public void visit(AndExpression n) { }

  @Override
  public void visit(CompareExpression n) { }

  @Override
  public void visit(PlusExpression n) { }

  @Override
  public void visit(MinusExpression n) { }

  @Override
  public void visit(TimesExpression n) { }

  @Override
  public void visit(ArrayLookup n) { }

  @Override
  public void visit(ArrayLength n) { }

  @Override
  public void visit(MessageSend n) { }

  @Override
  public void visit(ExpressionList n) { }

  @Override
  public void visit(ExpressionRest n) { }


  @Override
  public void visit(PrimaryExpression n) { }

  @Override
  public void visit(IntegerLiteral n) { }

  @Override
  public void visit(TrueLiteral n) { }

  @Override
  public void visit(FalseLiteral n) { }

  /**
   * f0 -> <IDENTIFIER>
   */
  @Override
  public void visit(Identifier n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    try {
      log.info("Creating symbol table with identifier: "+n.f0.tokenImage);
      // Put the identifier in a different symbol table, and put the new symbol table as an entry in the current
      // symbol table
      String identifier = n.f0.tokenImage;
      SymTable symTable = new SymTable(globalSymbolTable, identifier);
      globalSymbolTable.insert(identifier, symTable); // if the symbol already exists, this will throw an error
      log.info(log.RED("Added symbol table: "+identifier));

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();

    } catch (Exception e) {
      log.printStackTrace(e);
      System.exit(-1);
    }
    log.info("Left "+n.getClass().getSimpleName());
  }

  @Override
  public void visit(ThisExpression n) {

  }

  @Override
  public void visit(ArrayAllocationExpression n) {

  }

  @Override
  public void visit(AllocationExpression n) {

  }

  @Override
  public void visit(NotExpression n) {

  }

  @Override
  public void visit(BracketExpression n) {

  }
}
