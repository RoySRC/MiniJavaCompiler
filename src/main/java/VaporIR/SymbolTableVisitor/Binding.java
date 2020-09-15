package VaporIR.SymbolTableVisitor;

import core.syntaxtree.NodeToken;
import core.syntaxtree.Type;

/**
 * Each Symbol in the symbol table needs to have a set of binding information. These can include, but are not limited
 * to, the type of the symbol, size, offset, etc.
 */

public class Binding {

  private Type type;
  private int scope = SCOPE.NULL;
  private int scopeName = SCOPE.NULL;

  public Binding(Type type) {
    this.type = type;
  }

  public static class SCOPE {
    public static int NULL = Integer.MIN_VALUE;
    public static int PARAM = 0;
  }

  public static class TYPE {
    public static String BOOLEAN = "boolean";
    public static String INTEGER = "int";
    public static String INTEGER_ARRAY = "int[]";
  }

  public Binding(Type type, int scope) {
    this.type = type;
    this.scope = scope;
  }

  public String getStringType() {
    return ((NodeToken)this.type.f0.choice).tokenImage;
  }

  public Type getTypeObject() {
    return this.type;
  }

  public int getScope() {
    return this.scope;
  }

  public int getScopeName() {
    return this.scopeName;
  }

  public void setScope(int scope) {
    this.scope = scope;
  }

  public void setScopeName(int scopeName) {
    this.scopeName = scopeName;
  }
}
