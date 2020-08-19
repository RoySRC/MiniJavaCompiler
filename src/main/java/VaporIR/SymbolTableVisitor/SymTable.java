package VaporIR.SymbolTableVisitor;

import TypeChecking.TypeChecking.TypeCheckException;
import core.util.LOGGER;
import java.util.*;

import static core.util.util.isFunction;

/**
 * A hierarchical symbol table.
 *  https://www.cs.princeton.edu/courses/archive/spring08/cos217/asgts/symtable/
 *  https://www.tutorialspoint.com/compiler_design/compiler_design_symbol_table.htm
 *  http://www.cs.cornell.edu/courses/cs412/2008sp/lectures/lec12.pdf
 *  https://www.javatpoint.com/data-structure-for-symbol-table
 */

public class SymTable {

  // for logging
  private static final LOGGER log = new LOGGER(SymTable.class.getSimpleName(), true);

  // Children of the current symbol table by identifier
  private Map<String, SymTable> children = null;

  // Parent of the current symbol table
  private SymTable parent = null;

  // Each symbol in the symbol table needs to have a set of binding information
  private Map<String, Binding>  childrenBindingInformation = null;

  // Give the current symbol table a name
  private String name = null;

  // Get the last inserted symbol in the children of the current symbol table.
  private String lastInsertedSymbol = null;

  // An array list of function identifiers
  private ArrayList<Binding> functionIdentifiers = null;
  private int functionIdentifierPointer = 0;
  private Iterator<Binding> parameterListIterator = null;

  /**
   * Constructor
   * @param parent The parent of the current symbol table in the symbol table tree
   * @param identifier The name of the current symbol table
   */
  public SymTable(SymTable parent, String identifier) {
    this.parent = parent;
    this.children = new LinkedHashMap<>();
    this.childrenBindingInformation = new LinkedHashMap<>();
    this.name = identifier;
  }

  /**
   *
   * @param parent
   */
  public SymTable(SymTable parent) {
    this.parent = parent.parent;
    this.children = new LinkedHashMap<>();
    this.childrenBindingInformation = new LinkedHashMap<>();
    this.name = parent.name;
  }

  /**
   * Print the current symbol table and not its children
   */
  public void print() {
    System.out.printf("\u001B[47m\u001B[30m");
    String parent = null;
    if (this.parent == null)
      parent = "null";
    else
      parent = this.parent.getName();
    System.out.printf("%-67s", this.name+"  <"+parent+">");
    System.out.println(" \u001B[0m");

    System.out.printf("\u001b[4m"); // underline
    System.out.printf("%-25s  %-40s", "SYMBOL", "   BINDING");
    System.out.println("\u001B[0m");

    int i=0, j=this.children.size();
    for (String id : this.children.keySet()) {
      if (i == j-1)
        System.out.printf("\u001b[4m");

      String typeName = this.childrenBindingInformation.get(id).getStringType();
      int typeScope = this.childrenBindingInformation.get(id).getScope();
      int scopeName = this.childrenBindingInformation.get(id).getScopeName();
      System.out.printf("%25s  %40s", id+" ", "<"+typeName+", "+typeScope+", "+scopeName+">");

      if (i+1 != j)
        System.out.println();

      if (i == j-1)
        System.out.println("\u001B[0m");

      i++;
    }

    System.out.println("\n\n");

  }

  /**
   * Insert a symbol table entry (which points to a symbol table) into the current symbol table referenced by
   * identifier.
   * @param identifier This is the name of the current symbol
   * @param symbolTable Symbol table object to be referenced by the current entry that is to be put in the current
   *                    symbol table.
   * @throws Exception Throw an exception if the identifier is already present in the current symbol table
   */
  public void insert(String identifier, SymTable symbolTable) throws Exception {
    if (this.children.containsKey(identifier)) {
      log.error("Identifier '"+identifier+"' already present in symbol table.");
      throw new TypeCheckException("Identifier '"+identifier+"' already present in symbol table.");
    }
    this.children.put(identifier, symbolTable);
    this.lastInsertedSymbol = identifier;
  }

  /**
   * Insert the binding information for the current symbol identified by identifier into the current symbol table.
   * @param identifier This is the name of the current symbol
   * @param binding This is the binding of the symbol identified by identifier
   * @throws Exception Throw an exception if the identifier is already present in the current symbol table
   */
  public void insert(String identifier, Binding binding) throws Exception {
    if (this.childrenBindingInformation.containsKey(identifier)) {
      log.error("Identifier '"+identifier+"' already present in symbol table.");
      throw new TypeCheckException("Identifier '"+identifier+"' already present in symbol table.");
    }
    this.childrenBindingInformation.put(identifier, binding);
  }

  /**
   * Replace the binding information of the current identifier with a new binding information. Throws an error if the
   * identifier is not found.
   * @param identifier
   * @param new_binding
   * @throws Exception
   */
  public void replace(String identifier, Binding new_binding) throws Exception {
    if (!this.childrenBindingInformation.containsKey(identifier)) {
      throw new Exception(identifier+" not found in symbol table "+getName());
    }
    this.childrenBindingInformation.put(identifier, new_binding);
  }

  /**
   *
   * @param oldIdentifier
   * @param newIdentifier
   */
  public void replace(String oldIdentifier, String newIdentifier) throws Exception {
    if (!this.childrenBindingInformation.containsKey(oldIdentifier)) {
      throw new Exception(oldIdentifier+" not found in symbol table "+getName());
    }

    Binding oldBinding = this.childrenBindingInformation.remove(oldIdentifier);
    SymTable oldSymbolTable = this.children.remove(oldIdentifier);
    oldSymbolTable.name = newIdentifier;

    this.insert(newIdentifier, oldBinding);
    this.insert(newIdentifier, oldSymbolTable);
  }

  /**
   * Remove an entry from the symbol table
   * @param identifier
   */
  public void remove(String identifier) {
    this.childrenBindingInformation.remove(identifier);
    this.children.remove(identifier);
  }

  public Set<String> getSymbolKeySet() {
    return this.children.keySet();
  }

  /**
   * Traverse upwards from the current symbol table to the global symbol table (root symbol table) in the
   * hierarchical symbol table tree structure. If at any of the symbol table nodes the binding information of the @param
   * identifier exists, then return that binding information object. Otherwise return null when we have reached the
   * root of the symbol table tree.
   * table tree.
   * @param identifier
   * @return
   */
  public Binding lookup(String identifier) {
    log.info("Calling line: "+Thread.currentThread().getStackTrace()[2].getLineNumber());
    log.info("Looking for identifier: "+identifier+" in symbol table: "+getName());
    Binding binding = null;

    LinkedList<SymTable> symbolTableList = new LinkedList<>();
    symbolTableList.add(this);
    while (symbolTableList.size() != 0) {
      SymTable s = symbolTableList.remove();
      if (s == null)
        continue;
      log.info("Working with symbol table: "+s.getName());
      binding = s.getBindingInformation(identifier);
      if (binding != null) {
        log.info("Found binding information for identifier: "+identifier+" in symbol table: "+s.getName());
        return binding;
      }
      binding = null;
      // If i am at the global symbol table, and I have not found the identifier i am looking for, then ignore adding
      // the entries of the global symbol table as next candidate symbol tables to search for.
      if (s.getParent() == null) {
        log.warning("Could not find identifier: "+identifier);
        return binding;
      }

      // if the symbol is not found in the current table, schedule its parent classes to be searched next
      SymTable parentClass = s.getParentClass();
      if (parentClass != null) {
        log.info("Adding symbol table for " + parentClass.getName() + " to symbolTableList");
        symbolTableList.addFirst(parentClass);
      }

      // Also schedule the parent symbol table to be searched next
      symbolTableList.addLast(s.getParent());
    }

    return binding;
  }

  /**
   * Only looks at the current scope and the parent symbol table for the identifier. does not look at the parent class
   * symbol table
   * @param identifier
   * @return
   */
  public Binding lookupCurrentScope(String identifier) {
    log.info("Looking for identifier: "+identifier+" in symbol table: "+getName());
    Binding binding = null;

    Queue<SymTable> symbolTableList = new LinkedList<>();
    symbolTableList.add(this);
    while (symbolTableList.size() != 0) {
      SymTable s = symbolTableList.remove();
      if (s == null)
        continue;
      log.info("Working with symbol table: "+s.getName());
      binding = s.getBindingInformation(identifier);
      if (binding != null) {
        log.info("Found binding information for identifier: "+identifier);
        return binding;
      }
      binding = null;
      // If i am at the global symbol table, and I have not found the identifier i am looking for, then ignore adding
      // the entries of the global symbol table as next candidate symbol tables to search for.
      if (s.getParent() == null) {
        log.warning("Could not find identifier: "+identifier);
        return binding;
      }
      symbolTableList.add(s.getParent());
    }

    return binding;
  }

  /**
   * Only used for inheritance. Only looks up a parent scope for the binding information of the identifier
   * @param identifier
   * @return
   */
  public Binding lookupParentScope(String identifier) {
    // lookup the parent symbol table
    Queue<SymTable> q = new LinkedList<>();
    q.add(getParentClass());
    while (q.size() != 0) {
      SymTable p = q.remove();
      if (p == null) continue;
      q.add(p.getParentClass());
      Binding binding = p.getBindingInformation(identifier);
      if (binding != null) {
        return binding;
      }
    }

    return null;
  }

  /**
   * Get the symbol table of the parent class
   * @return
   */
  public SymTable getParentClass() {
    SymTable parent = null;
    for (String id : getSymbolKeySet()) {
      String type = getBindingInformation(id).getStringType();
      if (type == null) continue;
      if (type.equals("class")) {
        parent = children.get(id);
      }
    }
    return parent;
  }

  /**
   * Only looks for the identifier in the global symbol table and its children
   * @param identifier identifier to look up in the global scope
   * @return the binding information if the identifier
   * @throws TypeCheckException if there is a circular inheritance
   */
  public Binding globalSearch(String identifier) throws TypeCheckException {
    SymTable root = this;
    while (root!=null) root=root.getParent();
    Binding binding = null;
    for (String id : root.getSymbolKeySet()) {
      binding = root.getSymTable(id).lookup(identifier);
    }
    return binding;
  }

  /**
   * Get the binding information associated with an identifier
   * @param identifier
   * @return
   */
  public Binding getBindingInformation(String identifier) {
    return this.childrenBindingInformation.get(identifier);
  }

  /**
   * Get the {@link SymTable} associated with a given identifier. Search the current class scope and also the parent
   * class scope. This function also searches the parent symbol table of the hierarchical symbol table for the
   * identifier.
   * @param identifier
   * @return
   */
  public SymTable getSymTable(String identifier) throws TypeCheckException {
    SymTable retVal = null;
    LinkedList<SymTable> q = new LinkedList<>();
    Set<String> classes = new HashSet<>();
    classes.add(this.getName());
    q.add(this);
    while (q.size() != 0) {
      SymTable s = q.remove();
      retVal = s.children.get(identifier);
      if (retVal != null)
        return retVal;
      if (s.getParent() == null) { // I am working with the global symbol table
        continue;
      }

      // schedule the parent class of the current symbol table to be searched next
      SymTable parentClass = s.getParentClass();
      if (parentClass != null) {
        q.addFirst(parentClass);
        // If i have seen the parent before, then there is a circular inheritance
        if (!classes.contains(parentClass.getName())) {
          classes.add(parentClass.getName());
        } else {
          throw new TypeCheckException("Circular inheritance detected.");
        }
      }

      if (s.getParent() != null)
        q.addLast(s.getParent());
    }
    return retVal;
  }

  /**
   * Get the parent of the current symbol table.
   * @return
   */
  public SymTable getParent() {
    return this.parent;
  }

  /**
   * Get the identifier of the current symbol table, this is equivalent to the identifier
   * @return
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get identifier of the last inserted symbol in the children of the current symbol table
   * @return
   */
  public String getLastInsertedSymbol() {
    return lastInsertedSymbol;
  }

  public Map<String, SymTable> getChildren() {
    return this.children;
  }

  public void generateBindingInformationList(int scope) {
    log.info("getBindingInformationList(): "+getName());
    functionIdentifiers = new ArrayList<>();
    for (String id : getSymbolKeySet()) {
      Binding binding = getBindingInformation(id);
      if (binding == null || binding.getScope() == Binding.SCOPE.NULL)
        continue;
      log.info("Binding: "+binding);
      if (binding.getScope() == scope) {
        functionIdentifiers.add(binding);
      }
    }
  }

  public Binding getNextBindingInformation() {
    log.info(""+functionIdentifiers);
    return (functionIdentifierPointer < functionIdentifiers.size()) ?
        functionIdentifiers.get(functionIdentifierPointer++)
        : null;
  }

  public void resetBindingInformationCounter() {
    functionIdentifierPointer = 0;
  }

  /**
   * If the symbol table is a function, get the number of parameters
   * @return
   */
  public int getNumPrams() {
    int rv = 0;
    for (String id : getSymbolKeySet()) {
      Binding binding = getBindingInformation(id);
      if (binding == null || binding.getScope() == Binding.SCOPE.NULL)
        continue;
      if (binding.getScope() == Binding.SCOPE.PARAM)
        rv++;
    }
    return rv;
  }

  /**
   * Get the number of attributes in the current symbol table
   * @return
   */
  public int getNumAttributes() {
    int retVal = 0;
    for (String id : children.keySet()) {
      if (isFunction(id)) continue;
      retVal++;
    }
    return retVal;
  }

  /**
   * Get symbol table for all the functions in the current symbol table
   * @return A set of symbol tables for all the functions in the current symbol table
   */
  public Set<SymTable> getFunctions() {
    Set<SymTable> retVal = new LinkedHashSet<SymTable>();
    for (String id : childrenBindingInformation.keySet()) {
      if (!isFunction(id)) continue;
      retVal.add(children.get(id));
    }
    return retVal;
  }

  /**
   * If the current symbol table is a function symbol table, get a Set<> of binding information of the parameters
   * @return A Set<> containing the binding information of the parameters in the function symbol table.
   */
  public Set<Binding> getParameterList() {
    Set<Binding> retVal = new LinkedHashSet<Binding>();
    for (String id : childrenBindingInformation.keySet()) {
      Binding binding = childrenBindingInformation.get(id);
      if (binding.getScopeName() == Binding.SCOPE.PARAM) {
        retVal.add(binding);
      }
    }
    return retVal;
  }

  public Iterator<Binding> getParameterListIterator() {
    return getParameterList().iterator();
  }

}