package VaporMIR.RegisterAllocation;

import VaporMIR.Storage.Register.Register;
import cs132.vapor.ast.Node;
import core.util.Interval;
import core.util.LOGGER;

/**
 * This class, for every vapor variable stores its name and interval and which register stores the value o this variable
 */

public class VaporVariable {
  private static transient LOGGER log = new LOGGER(VaporVariable.class.getSimpleName(), true);
  public final String name;
  public Interval interval;
  public Register register;
  public boolean arg = false;
  public int line;
  public boolean persistent = false;

  public VaporVariable(String name, Interval interval, int line) {
    this.name = name;
    this.interval = interval;
    this.line = line;
  }

  public Register setRegister(Register newRgister) {
    register = newRgister;
    return register;
  }

  public Register getRegister() {
    return register;
  }

  public String toString() {
    return name;
  }
}
