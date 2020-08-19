import TypeChecking.PrettyPrintVisitor.PrettyPrintVisitor;
import VaporIR.SymbolTableVisitor.Binding;
import VaporIR.SymbolTableVisitor.SymTable;
import VaporIR.SymbolTableVisitor.SymbolTableBuilder;
import TypeChecking.TypeChecking.TypeCheckException;
import VaporIR.VaporVisitor.FunctionTableVisitor;
import VaporIR.VaporVisitor.FunctionVisitor;
import core.syntaxtree.Node;
import core.util.LOGGER;
import core.*;
import core.util.util;

import java.io.*;
import java.util.*;

public class J2V {
  // Logger
  private static transient LOGGER log = new LOGGER(J2V.class.getSimpleName(), true);

  private static MiniJavaParser parser = null;
  private LinkedList<String> finalVaporCode = new LinkedList<>();

  public J2V(InputStream iStream) {
    if (parser == null) parser = new MiniJavaParser(iStream);
    else parser.ReInit(iStream);
  }

  public J2V(InputStream iStream, boolean reinit) {
    if (reinit)
      parser.ReInit(iStream);
    else
      parser = new MiniJavaParser(iStream);
  }

  public LinkedList<String> getFinalVaporCode() {
    return this.finalVaporCode;
  }

  public void print() {
    for (String line : finalVaporCode) {
      System.out.println(line);
    }
  }

  public byte[] getByteArray() {
    StringBuilder sb = new StringBuilder();
    for (String line : finalVaporCode) {
      sb.append(line);
      sb.append("\n");
    }
    return sb.toString().getBytes();
  }

  /**
   * Generate the translation of minijava to vapor
   *
   * @throws ParseException
   */
  public void generateTranslation() throws Exception {
    log.info("Entered generateTranslation()");
    Node root = parser.Goal();

    prettyPrintMiniJava(root);

    // Build the symbol table
    log.info("Generating the miniJAVA symbol table");
    SymTable symTable = new SymbolTableBuilder(root).getSymbolTable();
    if (symTable == null) {
      log.error("There was an error building the symbol table.");
      return;
    } else {
      log.info("Done building the symbol table for miniJAVA.");
    }

    // Generate the inheritance link
    Map<String, LinkedList<String>> inheritanceLink = new HashMap<>();
    for (String id : symTable.getSymbolKeySet()) {
      SymTable s = symTable.getSymTable(id);
      inheritanceLink.put(id, new LinkedList<>());
      SymTable inh_ = s;
      while (inh_ != null) {
        SymTable p_inh_ = inh_.getParentClass();
        if (p_inh_ == null) break;
        inheritanceLink.get(id).add(p_inh_.getName());
        inh_ = p_inh_;
      }
    }
    if (log.getLogStatus())
      for (String s : inheritanceLink.keySet()) {
        System.out.print(s + " -> ");
        System.out.println(inheritanceLink.get(s));
      }

    // Replace all Binding with vapor binding
    this.replaceBindings(symTable);

    if (log.getLogStatus())
      SymbolTableBuilder.print(symTable);

    // Create the function table (Pass 1)
    FunctionTableVisitor pass1 = new FunctionTableVisitor(symTable, finalVaporCode);
    root.accept(pass1);


    // Create the functions
    FunctionVisitor pass2 = new FunctionVisitor(symTable, inheritanceLink, finalVaporCode);
    root.accept(pass2, null);


    log.info("Left generateTranslation()");
  }

  /**
   * @param symTable
   * @throws Exception
   */
  private void scopeResolution(SymTable symTable) throws Exception {
    Queue<SymTable> queue = new LinkedList<>();
    // put all the symbols in global symbol table in a queue
    for (String id : symTable.getSymbolKeySet()) {
      queue.add(symTable.getSymTable(id));
    }
    String _class = null;
    while (queue.size() != 0) {
      SymTable s = queue.remove();
      if (s.getParent().getName().equals("GLOBAL")) {
        _class = s.getName();
      }
      ArrayList<String> keySet = new ArrayList<>(s.getSymbolKeySet());
      for (String id : keySet) {
        String type = s.getBindingInformation(id).getStringType();
        int scope = s.getBindingInformation(id).getScope();
        if (!type.equals("class") && (scope == Binding.SCOPE.NULL || scope != Binding.SCOPE.PARAM)) {
          s.replace(id, _class + "." + id);
        }
      }
    }
  }

  /**
   * @param symTable
   * @throws Exception
   */
  private void encodeInheritanceInformation(SymTable symTable) throws Exception {
    for (String id : symTable.getSymbolKeySet()) {
      log.info("Working with class " + id);

      SymTable s = symTable.getSymTable(id);


      // Go to the base class
      LinkedList<SymTable> classHierarchy = new LinkedList<>();
      classHierarchy.add(s);
      SymTable classHierarchySymbolTable = classHierarchy.peekLast();
      while (classHierarchySymbolTable != null) {
        SymTable classHierarchyParentSymbolTable = classHierarchySymbolTable.getParentClass();
        if (classHierarchyParentSymbolTable == null) break;
        classHierarchy.add(classHierarchyParentSymbolTable);
        classHierarchySymbolTable = classHierarchy.peekLast();
      }

      // start copying attributes from the base class to the derived class
      SymTable baseClass = classHierarchy.removeLast();
      while (classHierarchy.size() != 0) {
        SymTable ss = classHierarchy.removeLast();
        for (String i : baseClass.getSymbolKeySet()) {
          log.info("Current symbol: " + i);
          Binding b = baseClass.getBindingInformation(i);
          SymTable newSymbolTable = new SymTable(ss, i);
          if (util.isFunction(i)) { // if i is a function, then copy all attributes from base class
            SymTable functionSymbolTable = baseClass.getSymTable(i);
            log.info("Current symbol is a function: " + functionSymbolTable.getName());
            // build sub symbol table tree
            for (String k : functionSymbolTable.getSymbolKeySet()) {
              log.info("\t identifier: " + k);
              Binding currentBinding = functionSymbolTable.getBindingInformation(k);
              newSymbolTable.insert(k, new SymTable(newSymbolTable, k));
              newSymbolTable.insert(k, new Binding(currentBinding.getTypeObject(), currentBinding.getScope()));
            }
          }
          // insert the new symbol table into the derived class
          ss.insert(i, newSymbolTable);
          ss.insert(i, new Binding(b.getTypeObject(), b.getScope()));
        }
        ss.remove(baseClass.getName());
        baseClass = ss;
      }

      log.info("Done with class " + id);
    }
  }

  /**
   * @param symTable
   */
  private void replaceBindings(SymTable symTable) throws Exception {
    // Replace the Binding with VaporBindingInformation
    Queue<SymTable> queue = new LinkedList<>();
    for (String id : symTable.getSymbolKeySet()) {
      queue.add(symTable.getSymTable(id));
    }
    while (!queue.isEmpty()) {
      SymTable s = queue.remove();
      List<String> keySet = new ArrayList<>(s.getSymbolKeySet());
      for (String id : keySet) {
        // migrate scope to scopeName
        s.getBindingInformation(id).setScopeName(s.getBindingInformation(id).getScope());

        // here setScope() represents setting the offset
        s.getBindingInformation(id).setScope(Binding.SCOPE.NULL);

        // schedule the function symbol tables to be replaced next
        if (util.isFunction(id)) {
          queue.add(s.getSymTable(id));
        }
      }
    }

    // calculate the offsets for the variable attributes
    this.calculateAttributeOffsets(symTable);

    // Calculate method offsets
    this.calculateMethodOffsets(symTable);
  }

  /**
   * @param symTable
   */
  private void calculateAttributeOffsets(SymTable symTable) throws TypeCheckException {
    int offset = 0;
    for (String _id : symTable.getSymbolKeySet()) {
      SymTable s = symTable.getSymTable(_id);
      List<String> keySet = new ArrayList<>(s.getSymbolKeySet());
      for (String id : keySet) {
        if (util.isFunction(id)) continue;
        s.getBindingInformation(id).setScope(offset * 4 + 4);
        offset++;
      }
      offset = 0;
    }
  }

  /**
   * @param symTable
   */
  private void calculateMethodOffsets(SymTable symTable) throws Exception {
    Queue<SymTable> queue = new LinkedList<>();
    for (String id : symTable.getSymbolKeySet()) {
      queue.add(symTable.getSymTable(id));
    }
    while (!queue.isEmpty()) {
      SymTable s = queue.remove();
      int offset = 0;
      List<String> keySet = new ArrayList<>(s.getSymbolKeySet());
      for (String id : keySet) {
        if (util.isFunction(id)) {
          s.getBindingInformation(id).setScope(offset * 4);
          offset++;
        }
      }
    }
  }

  /**
   * @param root
   */
  private void prettyPrintMiniJava(Node root) {
    if (log.getLogStatus()) {
      PrettyPrintVisitor prettyPrintVisitor = new PrettyPrintVisitor();
      root.accept(prettyPrintVisitor);
      prettyPrintVisitor.print();
    }
  }

  public static InputStream generateCode(String filename) throws Exception {
    log.info("Converting to Vapor IR.");
    J2V java2vapor = new J2V(new FileInputStream(new File(filename)), true);
    java2vapor.generateTranslation();
    InputStream outputStream = new ByteArrayInputStream(java2vapor.getByteArray());
    log.info("Vapor IR conversion successful.");
    return  outputStream;
  }

  public static void main(String[] args) throws Exception {
    log.info("Entered main program...");

    J2V java2vapor = new J2V(System.in);
    java2vapor.generateTranslation();
    java2vapor.print();


    log.info("Left main program...");
  }
}
