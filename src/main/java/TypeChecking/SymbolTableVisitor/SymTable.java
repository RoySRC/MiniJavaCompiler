package TypeChecking.SymbolTableVisitor;

import TypeChecking.TypeChecking.TypeCheckException;
import core.syntaxtree.NodeToken;
import core.util.LOGGER;

import java.util.*;

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
  private Map<String, BindingInformation>  childrenBindingInformation = null;

  // The depth of the current symbol table in the symbol table tree
  private int depth = 0;

  // Give the current symbol table a name
  private String name = null;

  // Get the last inserted symbol in the children of the current symbol table.
  private String lastInsertedSymbol = null;

  // An array list of function identifiers
  private ArrayList<BindingInformation> functionIdentifiers = null;
  private int functionIdentifierPointer = 0;

  /**
   * Constructor
   * @param parent The parent of the current symbol table in the symbol table tree
   * @param identifier The name of the current symbol table
   */
  public SymTable(SymTable parent, String identifier) {
    this.parent = parent;
    this.depth = (parent == null) ? 0 : parent.depth+1;
    this.children = new LinkedHashMap<>();
    this.childrenBindingInformation = new LinkedHashMap<>();
    this.name = identifier;
  }

  /**
   * Print the current symbol table and not its children
   */
  public void print() {
    System.out.printf("\u001B[47m\u001B[30m");
    System.out.printf("%-32s", this.name);
    System.out.println(" \u001B[0m");

    System.out.printf("\u001b[4m"); // underline
    System.out.printf("%-15s │%-15s ", "ID", "TYPE");
    System.out.println("\u001B[0m");

    int i=0, j=this.children.size();
    for (String id : this.children.keySet()) {
      if (i == j-1)
        System.out.printf("\u001b[4m");

      String typeName = ((NodeToken)(this.childrenBindingInformation.get(id).getType().f0.choice)).tokenImage;
      boolean typeScope = this.childrenBindingInformation.get(id).isParam();
      System.out.printf("%15s │%15s", id, "<"+typeName+", "+(typeScope?"param":"null")+">");

      if (i+1 != j)
        System.out.println();

      if (i == j-1)
        System.out.println("\u001B[0m");

      i++;
    }

    System.out.println();

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
  public void insert(String identifier, BindingInformation binding) throws Exception {
    if (this.childrenBindingInformation.containsKey(identifier)) {
      log.error("Identifier '"+identifier+"' already present in symbol table.");
      throw new TypeCheckException("Identifier '"+identifier+"' already present in symbol table.");
    }
    this.childrenBindingInformation.put(identifier, binding);
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
  public BindingInformation lookup(String identifier) {
    log.info("Looking for identifier: "+identifier+" in symbol table: "+getName());
    BindingInformation binding = null;

    Queue<SymTable> symbolTableList = new LinkedList<>();
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
        symbolTableList.add(parentClass);
      }

      // Also schedule the parent symbol table to be searched next
      symbolTableList.add(s.getParent());
    }

    return binding;
  }

  /**
   * Only looks at the current scope and the parent symbol table for the identifier.
   * @param identifier
   * @return
   */
  public BindingInformation lookupCurrentScope(String identifier) {
    log.info("Looking for identifier: "+identifier+" in symbol table: "+getName());
    BindingInformation binding = null;

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
  public BindingInformation lookupParentScope(String identifier) {
    // lookup the parent symbol table
    Queue<SymTable> q = new LinkedList<>();
    q.add(getParentClass());
    while (q.size() != 0) {
      SymTable p = q.remove();
      if (p == null) continue;
      q.add(p.getParentClass());
      BindingInformation binding = p.getBindingInformation(identifier);
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
      NodeToken node = (NodeToken) getBindingInformation(id).getType().f0.choice;
      String type = node.tokenImage;
      if (type.equals("class")) {
        parent = children.get(id);
      }
    }
    return parent;
  }

  /**
   * Get the binding information associated with an identifier
   * @param identifier
   * @return
   */
  public BindingInformation getBindingInformation(String identifier) {
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
    Queue<SymTable> q = new LinkedList<>();
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
        q.add(parentClass);
        // If i have seen the parent before, then there is a circular inheritance
        if (!classes.contains(parentClass.getName())) {
          classes.add(parentClass.getName());
        } else {
          throw new TypeCheckException("Circular inheritance detected.");
        }
      }

      if (s.getParent() != null)
        q.add(s.getParent());
    }
    return retVal;
  }

  public LinkedList<SymTable> getSymTableList(String identifier) throws TypeCheckException {
    LinkedList<SymTable> retVal = new LinkedList<>();
    SymTable S = null;
    Queue<SymTable> q = new LinkedList<>();
    Set<String> classes = new HashSet<>();
    classes.add(this.getName());
    q.add(this);
    while (q.size() != 0) {
      SymTable s = q.remove();
      S = s.children.get(identifier);

      if (S != null)
        retVal.add(S);

      if (s.getParent() == null) { // I am working with the global symbol table
        continue;
      }

      // schedule the parent class of the current symbol table to be searched next
      SymTable parentClass = s.getParentClass();
      if (parentClass != null) {
        q.add(parentClass);
        // If i have seen the parent before, then there is a circular inheritance
        if (!classes.contains(parentClass.getName())) {
          classes.add(parentClass.getName());
        } else {
          throw new TypeCheckException("Circular inheritance detected.");
        }
      }

      if (s.getParent() != null)
        q.add(s.getParent());
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

  /**
   * The following function scans a symbol table and returns a list of all the parameters. If the symbol table does
   * not contain any parameters, the function returns an empty list.
   * @return
   */
  public ArrayList<BindingInformation> getParametersList() {
    log.info("getBindingInformationList(): "+getName());
    functionIdentifiers = new ArrayList<>();
    for (String id : getSymbolKeySet()) {
      BindingInformation binding = getBindingInformation(id);
      if (binding == null || !binding.isParam())
        continue;
      log.info("Binding: "+binding);
      if (binding.isParam()) {
        functionIdentifiers.add(binding);
      }
    }
    return functionIdentifiers;
  }

  public BindingInformation getNextBindingInformation() {
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
      BindingInformation binding = getBindingInformation(id);
      if (binding == null || !binding.isParam())
        continue;
      if (binding.isParam())
        rv++;
    }
    return rv;
  }

}