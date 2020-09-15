package TypeChecking.SymbolTableVisitor;

import core.syntaxtree.Type;

/**
 * Each Symbol in the symbol table needs to have a set of binding information. These can include, but are not limited
 * to, the type of the symbol, size, offset, etc.
 */

public class BindingInformation {

  /**
   * Identifies the type of the current symbol. If the current symbol is a variable, then this field contains the
   * type of that variable. If the current symbol is a class, then this field contains 'class'. If the current symbol
   * is a function, then this field contains the return type of the function.
   */
  private Type type;

  /**
   * identifies whether or not the current symbol is a parameter
   */
  private boolean _isParam = false;

  public BindingInformation(Type type) {
    this.type = type;
  }

  public Type getType() {
    return this.type;
  }

  public boolean isParam() {
    return _isParam;
  }

  public void setIsParam() {
    _isParam = true;
  }

}
