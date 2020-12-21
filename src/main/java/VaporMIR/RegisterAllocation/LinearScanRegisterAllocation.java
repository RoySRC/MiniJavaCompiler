package VaporMIR.RegisterAllocation;

import VaporMIR.ControlFlowGraph.CFG;
import VaporMIR.Storage.Register.Callee;
import VaporMIR.Storage.Register.Caller;
import VaporMIR.Storage.Register.Register;
import VaporMIR.Storage.Stack.Local;
import VaporMIR.Storage.Stack.StackStorage;
import VaporMIR.Storage.Storage;
import cs132.vapor.ast.VFunction;
import core.util.LOGGER;

import java.util.*;

/**
 * External Sources:
 *   https://stackoverflow.com/questions/9268586/what-are-callee-and-caller-saved-registers
 *   http://www.cs.cornell.edu/courses/cs3410/2012sp/lecture/14-calling-w.pdf
 */

public class LinearScanRegisterAllocation {
  private static final transient LOGGER log = new LOGGER(LinearScanRegisterAllocation.class.getSimpleName(), true);

  public int localStackSize=0;

  /**
   * This is a map for maintaining the mapping of vapor variables as strings to registers and/or the local stack. The
   * following map will return null if a variable is defined but never used, if the key is a number or a label
   */
  public TreeMap<String, Storage> variableMap = new TreeMap<>();

  /**
   * Live intervals are stored in a list that is sorted in order of increasing start
   * point. The key is the start point, the value is the variable name
   */
  public LiveSet liveSet = new LiveSet();

  /**
   * The active list is kept sorted in order of increasing end point.
   * The key is the end point, the value is the variable name
   */
  public ActiveSet activeSet = new ActiveSet();

  /**
   * Store vapor variable and its location on the local stack
   */
  public Map<VaporVariable, Storage> localStackMap = new HashMap<>();

  /**
   * Create the set of free registers, initially all registers are free. We are not treating caller saved and callee
   * saved registers separately.
   */
  public Stack<Register> freeCallerRegisters = new Stack<Register>(){{
    for (int i=8; i>=0; --i) push(new Caller(i));
  }};

  public Stack<Register> freeCalleeRegisters = new Stack<Register>(){{
    for (int i=5; i>=0; --i) push(new Callee(i));
  }};

  public Map<Register, StackStorage> usedCalleeRegister = new HashMap<Register, StackStorage>();

  /**
   * Get the total number of usable registers
   */
  public final int TOTAL_REGISTERS = freeCallerRegisters.size()+freeCalleeRegisters.size();

  public Set<String> ignoredVariables = new HashSet<>();

  public CFG controlFlowGraph;
  public VFunction function;

  public LinearScanRegisterAllocation(VFunction function) throws Throwable {
    this.function = function;
    log.info("TOTAL_REGISTERS:"+TOTAL_REGISTERS);
    controlFlowGraph = new CFG(function); // create the CFG for the function
    for (VaporVariable var : controlFlowGraph.interval) {
      if (controlFlowGraph.args.contains(var.name)) { // the variable is an argument
        var.arg = true;
      }
      if (var.interval.isEmpty()) {
        log.error("Cannot add '"+var.name+"' to liveset since live interval is empty.");
        ignoredVariables.add(var.toString());
        continue;
      }
      log.info("Adding: "+var.name);
      liveSet.add(var);
    }
    log.info("liveset before: "+liveSet);

    allocRegister();
    log.info("liveSet:    "+liveSet);
    log.info("activeSet:  "+activeSet);
    log.info("localStack: "+localStackMap);
    log.info(localStackMap.keySet().toString());

    log.info("Printing register allocations for function "+function.ident+".");
    for (VaporVariable i : liveSet.data) {
      if (i.register != null) {
        variableMap.put(i.toString(), i.register);
      }
      if (localStackMap.get(i) != null) {
        variableMap.put(i.toString(), localStackMap.get(i));
      }
      log.info("\t"+i+": "+variableMap.get(i.toString()));
    }
  }

  /**
   *
   */
  public void allocRegister() {
    log.info("Entered LSRA algorithm.");
    for (VaporVariable i : liveSet.data) {
      log.info("Working with variable: "+i.toString());
      log.info("Free registers: "+ freeCallerRegisters);
      log.info("Persistance["+i+"]["+function.ident+"]: "+i.persistent);
      ExpireOldIntervals(i);
      if (freeCalleeRegisters.isEmpty() || freeCallerRegisters.isEmpty()) {
        log.info("No more free registers left, spilling...");
        SpillAtInterval(i);
      } else {
        Register freeRegister;
        if (i.persistent) {
          freeRegister = freeCalleeRegisters.pop();
          log.info("mapping "+i.name+" to local["+localStackSize+"]");
        } else {
          freeRegister = freeCallerRegisters.pop();
        }
        if (freeRegister instanceof Callee) {
          log.info("freeRegister instanceof Callee");
          if (!usedCalleeRegister.containsKey(freeRegister))
            usedCalleeRegister.put(freeRegister, new Local(localStackSize++));
        }
        log.info("removed free register: "+freeRegister);
        i.setRegister(freeRegister);
        log.info("activeSet before adding "+i.toString()+": "+activeSet);
        activeSet.add(i);
        log.info("activeSet after adding "+i.toString()+": "+activeSet);
      }
    }
  }

  /**
   *
   * @param i live interval
   */
  public void ExpireOldIntervals(VaporVariable i) {
    log.info("Expiring old interval with live interval: "+i.toString());
    for (VaporVariable j : activeSet.data) {
      if (j.interval.getEnd() >= i.interval.getStart()) {
        log.info(String.format("End point of %s >= startpoint of %s", j.toString(), i.toString()));
        return;
      }
      // remove j from active
      activeSet.remove(j);
      // add register[j] to pool of free registers
      log.info("Adding register "+j.getRegister()+" to free register pool");
      if (j.getRegister() instanceof Caller) {
        freeCallerRegisters.push(j.getRegister());
      } else {
        freeCalleeRegisters.push(j.getRegister());
      }
    }
    log.info("Leaving ExpireOldIntervals function.");
  }

  /**
   *
   * @param i live interval
   */
  public void SpillAtInterval(VaporVariable i) {
    log.info("Spilling.");
    VaporVariable spill = activeSet.data.get(activeSet.size()-1);
    if (spill.interval.getEnd() > i.interval.getEnd()) {
      //register[i] ← register[spill]
      i.setRegister(spill.getRegister());
      spill.setRegister(null); // spilled variable no longer has a register associated to it
      //location[spill] ← new stack location
      log.info("mapping "+spill.name+" to "+localStackSize);
      if (!localStackMap.containsKey(spill)) localStackMap.put(spill, new Local(localStackSize++));
      log.info("localStackMap: "+localStackMap.toString());
      log.info(localStackMap.keySet().toString());
      //remove spill from active
      activeSet.remove(spill);
      //add i to active, sorted by increasing end point
      activeSet.add(i);
    } else {
      //location[i] ← new stack location
      log.info("mapping "+i.name+" to "+localStackSize);
      if (!localStackMap.containsKey(i)) localStackMap.put(i, new Local(localStackSize++));
      log.info("localStackMap: "+localStackMap.toString());
      log.info(localStackMap.keySet().toString());
      i.setRegister(null); // variable 'i' no longer has a register associated to it
    }
    log.info("Current local: "+localStackMap);
  }
}
