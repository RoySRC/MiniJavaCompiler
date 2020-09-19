package VaporIR.VaporVisitor;

import TypeChecking.TypeChecking.TypeCheckException;
import VaporIR.SymbolTableVisitor.SymTable;
import core.syntaxtree.*;
import core.util.LOGGER;
import core.util.util;
import core.visitor.DepthFirstVisitor;

import java.util.LinkedList;

/**
 * This class builds the function table region for each base class in the final vapor code
 */

public class FunctionTableVisitor extends DepthFirstVisitor {
  //Logging
  private static final transient LOGGER log = new LOGGER(FunctionTableVisitor.class.getName());

  private SymTable symbolTable = null;
  private final LinkedList<String> finalVaporCode;
  private String indentation = "";
  private String currentClass = "";

  public FunctionTableVisitor(SymTable symTable, LinkedList<String> finalVaporCode) {
    this.symbolTable = symTable;
    this.finalVaporCode = finalVaporCode;
  }

  public LinkedList<String> getVaporCode() {
    return finalVaporCode;
  }

  public void print() {
    for (String line : finalVaporCode) {
      System.out.println(line);
    }
  }

  private void Indent() {
    this.indentation += "  ";
  }

  private void Outdent() {
    this.indentation = this.indentation.replaceFirst("  ", "");
  }

  private void setCurrentClass(String _class) {
    this.currentClass = _class;
  }

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   *
   * @param n

   */
  @Override
  public void visit(Goal n) {
    log.info("Entered "+n.getClass().getSimpleName());
    n.f1.accept(this);
    log.info("Left "+n.getClass().getSimpleName());
    
  }

  /**
   * f0 -> ClassDeclaration()
   * | ClassExtendsDeclaration()
   *
   * @param n

   */
  @Override
  public void visit(TypeDeclaration n) {
    log.info("Entered "+n.getClass().getSimpleName());
    n.f0.accept(this);
    log.info("Left "+n.getClass().getSimpleName());
    
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

   */
  @Override
  public void visit(ClassDeclaration n) {
    setCurrentClass(n.f1.f0.tokenImage);
    try {
      finalVaporCode.add(indentation+"const vmt_"+n.f1.f0.tokenImage);
      symbolTable = symbolTable.getSymTable(n.f1.f0.tokenImage);
      this.Indent();
      for (String id : symbolTable.getSymbolKeySet()) {
        if (util.isFunction(id)) {
          finalVaporCode.add(indentation+":"+currentClass+"."+id.substring(0, id.length()-2));
        }
      }
      this.Outdent();
      finalVaporCode.add("");
      symbolTable = symbolTable.getParent();

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
    }
    setCurrentClass("");
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
   *
   * @param n

   */
  @Override
  public void visit(ClassExtendsDeclaration n) {
    setCurrentClass(n.f1.f0.tokenImage);
    try {
      finalVaporCode.add(indentation+"const vmt_"+n.f1.f0.tokenImage);
      symbolTable = symbolTable.getSymTable(n.f1.f0.tokenImage);
      this.Indent();
      for (String id : symbolTable.getSymbolKeySet()) {
        if (util.isFunction(id)) {
          finalVaporCode.add(indentation+":"+currentClass+"."+id.substring(0, id.length()-2));
        }
      }
      this.Outdent();
      finalVaporCode.add("");
      symbolTable = symbolTable.getParent();

    } catch (TypeCheckException e) {
      log.printStackTrace(e);
    }
    setCurrentClass("");
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

   */
  @Override
  public void visit(MethodDeclaration n) {
    Indent();
    finalVaporCode.add(indentation+":"+currentClass+"."+n.f2.f0.tokenImage);
    Outdent();
  }
}
