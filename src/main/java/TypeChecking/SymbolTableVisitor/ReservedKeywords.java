package TypeChecking.SymbolTableVisitor;

import java.util.HashSet;
import java.util.Set;

public class ReservedKeywords {

  private static final Set<String> reservedWords = new HashSet<String>(){{
    add("int");
    add("int[]");
    add("boolean");
    add("this");
  }};

  public static boolean contains(String word) {
    return reservedWords.contains(word);
  }

  public static void add(String word) {
    reservedWords.add(word);
  }

}
