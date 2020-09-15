import TypeChecking.MessagePrinter.MessagePrinter;
import TypeChecking.PrettyPrintVisitor.PrettyPrintVisitor;
import TypeChecking.SymbolTableVisitor.SymTable;
import TypeChecking.SymbolTableVisitor.SymbolTableBuilder;
import TypeChecking.TypeChecking.TypeCheckVisitor;
import core.syntaxtree.Node;
import core.*;
import core.util.LOGGER;

import java.io.InputStream;

public class Typecheck {

  // for logging
  private static transient final LOGGER log = new LOGGER(Typecheck.class.getSimpleName());

  private static MiniJavaParser parser = null;

  public Typecheck(InputStream iStream) {
    parser = new MiniJavaParser(iStream);
  }

  public Typecheck(MiniJavaParser parser) {
    this.parser = parser;
  }

  /**
   * Check if a program Typechecks.
   * @return True if the program typechecks, false otherwise
   */
  public boolean check() {
    try {
      Node root = parser.Goal();

      if (log.getLogStatus()) {
        PrettyPrintVisitor prettyPrintVisitor = new PrettyPrintVisitor();
        root.accept(prettyPrintVisitor);
        prettyPrintVisitor.print();
      }

      log.info("--------------------------------------------------------------------------------------------");

      // Build the symbol table and also type check it
      SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder(root);
      if (symbolTableBuilder.getErrorStatus() == true)
         return false;
      SymTable symbolTable = symbolTableBuilder.getSymbolTable();
      if (log.getLogStatus())
        symbolTableBuilder.print(symbolTable);

      // Check the type of each statement, function return types, etc.
      TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(symbolTable);
      root.accept(typeCheckVisitor);
      log.info("Error Status: "+typeCheckVisitor.getErrorStatus());
      return !typeCheckVisitor.getErrorStatus();

    } catch (ParseException e) {
      log.printStackTrace(e);
      return false;
    }
  }

  public static void main(String[] args) {
    Typecheck typecheck = new Typecheck(System.in);
    boolean programTypeChecks = typecheck.check();

    if (programTypeChecks)
      MessagePrinter.printSuccess();
    else
      MessagePrinter.printFailure();
  }

}
