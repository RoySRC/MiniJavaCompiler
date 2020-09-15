package VaporMIR.RegisterAllocation;

import java.util.ArrayList;
import java.util.Collections;

public class LiveSet {
  public ArrayList<VaporVariable> data = new ArrayList<>();

  public LiveSet() {}

  public VaporVariable add(VaporVariable e) {
    data.add(e);
    Collections.sort(data, (o1, o2) -> o1.interval.getStart()-o2.interval.getStart());
    return e;
  }

  public VaporVariable remove(VaporVariable e) {
    data.remove(e);
    Collections.sort(data, (o1, o2) -> o1.interval.getStart()-o2.interval.getStart());
    return e;
  }

  public int size() {
    return data.size();
  }

  public String toString() {
    return data.toString();
  }
}
