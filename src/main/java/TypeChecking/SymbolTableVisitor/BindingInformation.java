package TypeChecking.SymbolTableVisitor;

import core.syntaxtree.Type;

public /**
 * Each Symbol in the symbol table needs to have a set of binding information. These can include, but are not limited
 * to, the type of the symbol, size, offset, etc.
 */
class BindingInformation {

  private Type type;
  private String scope = null;

  public BindingInformation(Type type) {
    this.type = type;
  }

  public BindingInformation(Type type, String scope) {
    this.type = type;
    this.scope = scope;
  }

  public Type getType() {
    return this.type;
  }

  public String getScope() {
    return this.scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }
}
