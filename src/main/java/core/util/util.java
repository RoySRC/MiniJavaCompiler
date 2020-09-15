package core.util;

import VaporIR.SymbolTableVisitor.ReservedKeywords;
import core.syntaxtree.*;

public class util {

    /**
     * Check whether or not if an identifier is a function
     * @param identifier the identifier to check
     * @return true if the identifier is a function, false otherwise
     */
    public static boolean isFunction(String identifier) {
        return (identifier.length()>=2 && identifier.substring(identifier.length()-2).equals("()"));
    }

    /**
     * Deduce if the return type is a class.
     * @param type of return statement
     * @return true if @type is of class, false otherwise
     */
    public static boolean isClass(Type type) {
        if (type.f0.choice instanceof Identifier)
            return !ReservedKeywords.contains(((Identifier)type.f0.choice).f0.tokenImage);
        else
            return false;
    }

    /**
     * Adapted from https://mkyong.com/java/java-how-to-check-if-a-string-is-numeric/
     * @param s
     * @return
     */
    public static boolean isNumeric(String s) {
        for (char c : s.toCharArray())
            if (!Character.isDigit(c))
                return false;
        return true;
    }
}
