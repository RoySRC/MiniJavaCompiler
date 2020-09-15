package TypeChecking.PrettyPrintVisitor;

import core.syntaxtree.*;
import core.util.LOGGER;
import core.visitor.Visitor;
import java.util.Enumeration;

/**
 * This class has almost the same implementation as DepthFirstVisitor class, but when a node is
 * visited, it is added to the TreeStructure member variable to build a depth first spanning
 * tree.
 */
public class PrettyPrintVisitor implements Visitor {

  private TreeStructure treeStructure = new TreeStructure();

  // for logging
  private static final LOGGER log = new LOGGER(PrettyPrintVisitor.class.getSimpleName());

  /**
   * Get the tree structure built by this class
   * @return
   */
  public TreeStructure getTreeStructure() {
    return treeStructure;
  }

  public void print() {
    this.treeStructure.print();
  }

  //
  // Auto class visitors--probably don't need to be overridden.
  //
  public void visit(NodeList n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
      e.nextElement().accept(this);
    treeStructure.decrementDepth();
  }

  public void visit(NodeListOptional n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    if ( n.present() )
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
        e.nextElement().accept(this);
    treeStructure.decrementDepth();
  }

  public void visit(NodeOptional n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    if ( n.present() )
      n.node.accept(this);
    treeStructure.decrementDepth();
  }

  public void visit(NodeSequence n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); )
      e.nextElement().accept(this);
    treeStructure.decrementDepth();
  }

  public void visit(NodeToken n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    treeStructure.decrementDepth();
  }

  //
  // User-generated core.visitor methods below
  //

  /**
   * f0 -> MainClass()
   * f1 -> ( TypeDeclaration() )*
   * f2 -> <EOF>
   */
  public void visit(Goal n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
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
  public void visit(MainClass n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    n.f7.accept(this);
    n.f8.accept(this);
    n.f9.accept(this);
    n.f10.accept(this);
    n.f11.accept(this);
    n.f12.accept(this);
    n.f13.accept(this);
    n.f14.accept(this);
    n.f15.accept(this);
    n.f16.accept(this);
    n.f17.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> ClassDeclaration()
   *       | ClassExtendsDeclaration()
   */
  public void visit(TypeDeclaration n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "class"
   * f1 -> Identifier()
   * f2 -> "{"
   * f3 -> ( VarDeclaration() )*
   * f4 -> ( MethodDeclaration() )*
   * f5 -> "}"
   */
  public void visit(ClassDeclaration n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    treeStructure.decrementDepth();
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
  public void visit(ClassExtendsDeclaration n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    n.f7.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   * f2 -> ";"
   */
  public void visit(VarDeclaration n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
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
  public void visit(MethodDeclaration n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    n.f7.accept(this);
    n.f8.accept(this);
    n.f9.accept(this);
    n.f10.accept(this);
    n.f11.accept(this);
    n.f12.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> FormalParameter()
   * f1 -> ( FormalParameterRest() )*
   */
  public void visit(FormalParameterList n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> Type()
   * f1 -> Identifier()
   */
  public void visit(FormalParameter n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> ","
   * f1 -> FormalParameter()
   */
  public void visit(FormalParameterRest n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> ArrayType()
   *       | BooleanType()
   *       | IntegerType()
   *       | Identifier()
   */
  public void visit(Type n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "int"
   * f1 -> "["
   * f2 -> "]"
   */
  public void visit(ArrayType n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "boolean"
   */
  public void visit(BooleanType n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "int"
   */
  public void visit(IntegerType n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> Block()
   *       | AssignmentStatement()
   *       | ArrayAssignmentStatement()
   *       | IfStatement()
   *       | WhileStatement()
   *       | PrintStatement()
   */
  public void visit(Statement n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "{"
   * f1 -> ( Statement() )*
   * f2 -> "}"
   */
  public void visit(Block n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> Identifier()
   * f1 -> "="
   * f2 -> Expression()
   * f3 -> ";"
   */
  public void visit(AssignmentStatement n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    treeStructure.decrementDepth();
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
  public void visit(ArrayAssignmentStatement n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    treeStructure.decrementDepth();
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
  public void visit(IfStatement n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    n.f6.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> Statement()
   */
  public void visit(WhileStatement n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "System.out.println"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> ";"
   */
  public void visit(PrintStatement n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    treeStructure.decrementDepth();
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
  public void visit(Expression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "&&"
   * f2 -> PrimaryExpression()
   */
  public void visit(AndExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "<"
   * f2 -> PrimaryExpression()
   */
  public void visit(CompareExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "+"
   * f2 -> PrimaryExpression()
   */
  public void visit(PlusExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "-"
   * f2 -> PrimaryExpression()
   */
  public void visit(MinusExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "*"
   * f2 -> PrimaryExpression()
   */
  public void visit(TimesExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "["
   * f2 -> PrimaryExpression()
   * f3 -> "]"
   */
  public void visit(ArrayLookup n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> "length"
   */
  public void visit(ArrayLength n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> PrimaryExpression()
   * f1 -> "."
   * f2 -> Identifier()
   * f3 -> "("
   * f4 -> ( ExpressionList() )?
   * f5 -> ")"
   */
  public void visit(MessageSend n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    n.f5.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> Expression()
   * f1 -> ( ExpressionRest() )*
   */
  public void visit(ExpressionList n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> ","
   * f1 -> Expression()
   */
  public void visit(ExpressionRest n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    treeStructure.decrementDepth();
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
  public void visit(PrimaryExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> <INTEGER_LITERAL>
   */
  public void visit(IntegerLiteral n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "true"
   */
  public void visit(TrueLiteral n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "false"
   */
  public void visit(FalseLiteral n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> <IDENTIFIER>
   */
  public void visit(Identifier n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "this"
   */
  public void visit(ThisExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "new"
   * f1 -> "int"
   * f2 -> "["
   * f3 -> Expression()
   * f4 -> "]"
   */
  public void visit(ArrayAllocationExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    n.f4.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "new"
   * f1 -> Identifier()
   * f2 -> "("
   * f3 -> ")"
   */
  public void visit(AllocationExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    n.f3.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "!"
   * f1 -> Expression()
   */
  public void visit(NotExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    treeStructure.decrementDepth();
  }

  /**
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   */
  public void visit(BracketExpression n) {
    treeStructure.incrementDepth();
    treeStructure.addNode(n);
    n.f0.accept(this);
    n.f1.accept(this);
    n.f2.accept(this);
    treeStructure.decrementDepth();
  }

}
