package TypeChecking.SymbolTableVisitor;

import core.syntaxtree.Node;
import core.util.LOGGER;
import java.util.LinkedList;
import java.util.Queue;

public class SymbolTableBuilder {

  // for logging
  private static final transient LOGGER log = new LOGGER(SymbolTableBuilder.class.getSimpleName());

  private boolean errorStatus = false;
  private SymTable symbolTable;

  /**
   *
   * @param root
   */
  public SymbolTableBuilder(Node root) {
    symbolTable = this.buildSymbolTable(root);
  }

  /**
   *
   * @return
   */
  public SymTable getSymbolTable() {
    return this.symbolTable;
  }

  /**
   *
   * @return
   */
  public boolean getErrorStatus() {
    return this.errorStatus;
  }

  /**
   * Breadth first printer of the hierarchical symbol table data structure.
   * @param symbolTable
   */
  public void print(SymTable symbolTable) {
    log.info("Entered print() statement");
    symbolTable.print();
    Queue<SymTable> q = new LinkedList<>();
    q.add(symbolTable);
    while ( q.size() != 0 ) {
      SymTable node = q.remove();
      for (String id : node.getChildren().keySet()) {
        SymTable child = node.getChildren().get(id);
        if (child != null) {
          child.print();
          q.add(child);
        }
      }
    }
    log.info("Left print() statement.");
    log.info(log.RED("Current Symbol Table pointer: "+symbolTable.getName()));
  }

  /**
   *
   * @param root
   * @return
   */
  private SymTable pass1(Node root) {
    SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
    root.accept(symbolTableVisitor);
    if (symbolTableVisitor.getErrorStatus()) errorStatus = true;
    return symbolTableVisitor.getSymbolTable();
  }

  /**
   *
   * @param S The symbol table from pass1
   * @param root
   * @return
   */
  private SymTable pass2(SymTable S, Node root) {
    SymbolTableInheritanceVisitor symbolTableInheritanceVisitor = new SymbolTableInheritanceVisitor(S);
    root.accept(symbolTableInheritanceVisitor);
    if (symbolTableInheritanceVisitor.getErrorStatus()) errorStatus = true;
    return symbolTableInheritanceVisitor.getSymbolTable();
  }

  /**
   *
   * @param S
   * @param root
   */
  private void pass3(SymTable S, Node root) {
    SymbolTableInheritanceTypeCheckVisitor symbolTableInheritanceTypeCheckVisitor;
    symbolTableInheritanceTypeCheckVisitor = new SymbolTableInheritanceTypeCheckVisitor(S);
    root.accept(symbolTableInheritanceTypeCheckVisitor);
    if (symbolTableInheritanceTypeCheckVisitor.getErrorStatus()) errorStatus = true;
  }

  /**
   *
   * @param root
   * @return
   */
  private SymTable buildSymbolTable(Node root) {
    errorStatus = false;

    // first pass of the symbol table
    SymTable symbolTablePass1 = pass1(root);
    if (errorStatus)
      return null;

    // second pass of the symbol table
    SymTable symbolTablePass2 = pass2(symbolTablePass1, root);
    if (errorStatus)
      return null;

    // The following typechecks the symbol table
    pass3(symbolTablePass2, root);
    if (errorStatus)
      return null;


    return symbolTablePass2;
  }

}
