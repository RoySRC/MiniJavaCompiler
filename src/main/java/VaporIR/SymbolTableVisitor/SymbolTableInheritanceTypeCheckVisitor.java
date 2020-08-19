package VaporIR.SymbolTableVisitor;

import TypeChecking.TypeChecking.TypeCheckException;
import core.syntaxtree.*;
import core.util.LOGGER;
import core.visitor.DepthFirstVisitor;

import java.util.*;

public class SymbolTableInheritanceTypeCheckVisitor extends DepthFirstVisitor {

  // for logging
  private static final transient LOGGER log = new LOGGER(SymbolTableInheritanceTypeCheckVisitor.class.getSimpleName());

  private SymTable symbolTable = null;
  private boolean errorStatus = false;

  public SymbolTableInheritanceTypeCheckVisitor(SymTable S) {
    this.symbolTable = S;
  }

  public boolean getErrorStatus() {
    return errorStatus;
  }

  private void setErrorStatus() {
    this.errorStatus = true;
  }


  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  @Override
  public void visit(Goal n) {
    if (getErrorStatus()) return;
    n.f1.accept(this);
    if (getErrorStatus()) return;
    log.info("Done typechecking inheritance symbol table");
  }

  /**
   * f0 -> ClassDeclaration()
   *       | ClassExtendsDeclaration()
   */
  @Override
  public void visit(TypeDeclaration n) {
    if (getErrorStatus()) return;
    log.info("Entered "+n.getClass().getSimpleName());
    if (n.f0.choice instanceof ClassExtendsDeclaration) {
      log.info("Entering "+n.f0.choice.getClass().getSimpleName());
      n.f0.accept(this);
      if (getErrorStatus()) return;
    }
    log.info("left "+n.getClass().getSimpleName());
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
      log.info("Entered try block.");
      // move to the class symbol table
      symbolTable = symbolTable.getSymTable(n.f1.f0.tokenImage);
      log.info(log.RED(String.format("Current symbol table pointer: %s", symbolTable.getName())));


      // check for circular inheritance
      Set<String> classes = new HashSet<>();
      classes.add(symbolTable.getName());
      SymTable parentClass = symbolTable.getParentClass();
      while (parentClass != null) {
        log.info("Working with current class: "+parentClass.getName());
        if (!classes.contains(parentClass.getName())) {
          log.info(parentClass.getName()+" not seen before");
          classes.add(parentClass.getName());
        } else {
          log.error(parentClass.getName()+" encountered before");
          throw new TypeCheckException("Circular inheritance detected.");
        }
        parentClass = parentClass.getParentClass();
      }

      // visit the variable declarations
      n.f5.accept(this);
      if (getErrorStatus()) return;

      // visit the method declarations
      n.f6.accept(this);
      if (getErrorStatus()) return;

      // move back to the global symbol table
      symbolTable = symbolTable.getParent();
      log.info(log.RED(String.format("Current symbol table pointer: %s", symbolTable.getName())));

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();
    }
    log.info("Exiting "+n.getClass().getSimpleName());
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
      // get the symbol table of the parent of the current class
      SymTable parent = symbolTable.getParentClass();

      // check to make sure that the parent has been declared
      if (!symbolTable.getParent().getChildren().containsKey(parent.getName()))
        throw new TypeCheckException(String.format("Class %s has not been declared.", parent.getName()));
      else
        log.info(String.format("Class %s declaration found.", parent.getName()));

      // Make sure that the same variable is not declared in the parent class
//      if (symbolTable.lookupParentScope(n.f1.f0.tokenImage) != null)
//        throw new TypeCheckException("Identifier "+n.f1.f0.tokenImage+" already exists in parent class.");

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
    log.info("Entered "+n.getClass().getSimpleName());
    try {
//      // get the symbol table of the parent of the current class
//      SymTable parent = symbolTable.getParentClass();
//
//      log.info("Parent Class: "+parent.getName());
//
//      String methodName = n.f2.f0.tokenImage+"()";
//      if (symbolTable.lookupParentScope(methodName) != null)
//        throw new TypeCheckException("Function "+methodName+" already exists in parent class.");

    } catch (Exception e) {
      log.printStackTrace(e);
      setErrorStatus();
      System.exit(-1);
    }
    log.info("Left "+n.getClass().getSimpleName());
  }
}
