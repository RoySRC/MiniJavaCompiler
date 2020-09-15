package core.util;

import java.util.TreeSet;

/**
 * This object acts as an encapsulation for the interval of each variable in the IR. This object contains the start
 * and the end lines for each variable, indicating the line when it was declared and the line of its last use.
 */
public class Interval {

  /**
   * The list of lines where the current variable in question is live
   */
  public TreeSet<Integer> interval = new TreeSet<>(Integer::compareTo);
  /**
   * The name of the variable that this interval belongs to
   */
  public String variableName;

  public Interval(String variableName) {
    this.variableName = variableName;
  }

  public void add(int in) {
    interval.add(in);
  }

  public boolean isEmpty() {
    return interval.isEmpty();
  }

  public int getStart() {
//    int i=0;
//    for (int k : interval) {
//      if (i==1) {
//        return k;
//      }
//      ++i;
//    }
    return interval.first();
  }

  public int getEnd() {
//    int i=0;
//    for (int k : interval) {
//      if (i==interval.size()-2) {
//        return k;
//      }
//      ++i;
//    }
    return interval.last();
  }

  public String toString() {
    return interval.toString();
  }

}
