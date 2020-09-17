package TypeChecking.SymbolTableVisitor;

import TypeChecking.TypeChecking.TypeCheckException;
import core.syntaxtree.*;
import core.util.LOGGER;
import core.visitor.DepthFirstVisitor;

/**
 * This symbol table core.visitor deals with inheritance. It goes through every class that inherits from another class,
 * and checks to make sure that the parent class is present in the global symbol table. Once it verifies that the
 * parent class is present in the global symbol table it checks to make sure that the same set of variables and
 * function identifiers are not declared in both classes. This symbol table core.visitor only visits the
 * ClassExtendsDeclaration.
 */

public class SymbolTableInheritanceVisitor extends DepthFirstVisitor {
  // for logging
  private static final transient LOGGER log = new LOGGER(SymbolTableInheritanceVisitor.class.getSimpleName());

  private SymTable globalSymbolTable = null;
  private boolean errorStatus = false;

  public SymbolTableInheritanceVisitor(SymTable initialSymbolTable) {
    this.globalSymbolTable = initialSymbolTable;
  }

  public SymTable getSymbolTable() {
    return this.globalSymbolTable;
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
   */
  private void setErrorStatus() {
    this.errorStatus = true;
  }

  //
  // Needs to be written by user
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
    n.f1.accept(this);
    if (getErrorStatus()) return;
    log.info("Left "+n.getClass().getSimpleName());
  }

  /**
   * f0 -> ClassDeclaration()
   *       | ClassExtendsDeclaration()
   */
  @Override
  public void visit(TypeDeclaration n) {
    if (getErrorStatus()) return;
    // Only visit ClassExtendsDeclaration. This is because we want to modify the symbol table for classes that extend
    // other classes, i.e. we want to add a class to the symbol table for child classes.
    if (n.f0.choice instanceof ClassExtendsDeclaration) {
      n.f0.accept(this);
      if (getErrorStatus()) return;
    }
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
    try {
      // Check to make sure that parent class exists
      if (!globalSymbolTable.getChildren().containsKey(n.f3.f0.tokenImage)) {
        throw new TypeCheckException("Class inheritance, parent class not found.");
      }

      // Insert the parent into the child symbol table
      String parentClassIdentifier = n.f3.f0.tokenImage;
      SymTable classSymbolTable = globalSymbolTable.getSymTable(n.f1.f0.tokenImage);
      classSymbolTable.insert(parentClassIdentifier, globalSymbolTable.getSymTable(parentClassIdentifier));
      Type t = new Type( new NodeChoice( new NodeToken("class") ) );
      BindingInformation binding = new BindingInformation(t);
      classSymbolTable.insert(parentClassIdentifier, binding);

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
      setErrorStatus();

    } catch (Exception e) {
      log.printStackTrace(e);
      System.exit(-1);
    }
  }

}
