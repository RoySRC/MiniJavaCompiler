package VaporIR.VaporVisitor;

import VaporIR.SymbolTableVisitor.SymTable;
import VaporIR.SymbolTableVisitor.SymbolTableBuilder;
import TypeChecking.TypeChecking.TypeCheckException;
import core.syntaxtree.*;
import core.util.LOGGER;
import core.util.util;
import core.visitor.DepthFirstVisitor;
import core.visitor.GJDepthFirst;
import core.visitor.GJVisitor;

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

   */
  @Override
  public void visit(MainClass n) {
    
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
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   *
   * @param n

   */
  @Override
  public void visit(VarDeclaration n) {
    
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

  /**
   * f0 -> FormalParameter()
   * f1 -> ( FormalParameterRest() )*
   *
   * @param n

   */
  @Override
  public void visit(FormalParameterList n) {
    
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   *
   * @param n

   */
  @Override
  public void visit(FormalParameter n) {
    
  }

  /**
   * f0 -> ","
   * f1 -> FormalParameter()
   *
   * @param n

   */
  @Override
  public void visit(FormalParameterRest n) {
    
  }

  /**
   * f0 -> ArrayType()
   * | BooleanType()
   * | IntegerType()
   * | Identifier()
   *
   * @param n

   */
  @Override
  public void visit(Type n) {
    
  }

  /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   *
   * @param n

   */
  @Override
  public void visit(ArrayType n) {
    
  }

  /**
   * f0 -> "boolean"
   *
   * @param n

   */
  @Override
  public void visit(BooleanType n) {
    
  }

  /**
   * f0 -> "int"
   *
   * @param n

   */
  @Override
  public void visit(IntegerType n) {
    
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

   */
  @Override
  public void visit(Statement n) {
    
  }

  /**
   * f0 -> "{"
   * f1 -> ( Statement() )*
   * f2 -> "}"
   *
   * @param n

   */
  @Override
  public void visit(Block n) {
    
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Expression()
   * f3 -> ";"
   *
   * @param n

   */
  @Override
  public void visit(AssignmentStatement n) {
    
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

   */
  @Override
  public void visit(ArrayAssignmentStatement n) {
    
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

   */
  @Override
  public void visit(IfStatement n) {
    
  }

  /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   *
   * @param n

   */
  @Override
  public void visit(WhileStatement n) {
    
  }

  /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   *
   * @param n

   */
  @Override
  public void visit(PrintStatement n) {
    
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
   * @param n

   */
  @Override
  public void visit(Expression n) {
    
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   *
   * @param n

   */
  @Override
  public void visit(AndExpression n) {
    
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   *
   * @param n

   */
  @Override
  public void visit(CompareExpression n) {
    
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   *
   * @param n

   */
  @Override
  public void visit(PlusExpression n) {
    
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   *
   * @param n

   */
  @Override
  public void visit(MinusExpression n) {
    
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   *
   * @param n

   */
  @Override
  public void visit(TimesExpression n) {
    
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   *
   * @param n

   */
  @Override
  public void visit(ArrayLookup n) {
    
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   *
   * @param n

   */
  @Override
  public void visit(ArrayLength n) {
    
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   *
   * @param n

   */
  @Override
  public void visit(MessageSend n) {
    
  }

  /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )*
   *
   * @param n

   */
  @Override
  public void visit(ExpressionList n) {
    
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   *
   * @param n

   */
  @Override
  public void visit(ExpressionRest n) {
    
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

   */
  @Override
  public void visit(PrimaryExpression n) {
    
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   *
   * @param n

   */
  @Override
  public void visit(IntegerLiteral n) {
    
  }

  /**
   * f0 -> "true"
   *
   * @param n

   */
  @Override
  public void visit(TrueLiteral n) {
    
  }

  /**
   * f0 -> "false"
   *
   * @param n

   */
  @Override
  public void visit(FalseLiteral n) {
    
  }

  /**
   * f0 -> <IDENTIFIER>
   *
   * @param n

   */
  @Override
  public void visit(Identifier n) {
    
  }

  /**
   * f0 -> "this"
   *
   * @param n

   */
  @Override
  public void visit(ThisExpression n) {
    
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   *
   * @param n

   */
  @Override
  public void visit(ArrayAllocationExpression n) {
    
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   *
   * @param n

   */
  @Override
  public void visit(AllocationExpression n) {
    
  }

  /**
   * f0 -> "!"
   * f1 -> Expression()
   *
   * @param n

   */
  @Override
  public void visit(NotExpression n) {
    
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   *
   * @param n

   */
  @Override
  public void visit(BracketExpression n) {
    
  }
}
