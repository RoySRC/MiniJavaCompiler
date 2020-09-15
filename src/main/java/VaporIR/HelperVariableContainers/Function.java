package VaporIR.HelperVariableContainers;

import VaporIR.SymbolTableVisitor.SymTable;
import core.util.LOGGER;

/**
 * This class is only used for message send expression
 */
public class Function {
    private static final transient LOGGER log = new LOGGER(Function.class.getSimpleName());

    public SymTable s;
    public int parameterCount;

    public void decrementParameterCount() {
        int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
        log.info("Decrementing parameter count for function: "+s.getName(), line);
        parameterCount -= 1;
        log.info("Final remaining number of parameters: "+parameterCount, line);
    }

    public void incrementParameterCount() {
        int line = Thread.currentThread().getStackTrace()[2].getLineNumber();
        log.info("Incrementing parameter count for function: "+s.getName(), line);
        parameterCount += 1;
        log.info("Final remaining number of parameters: "+parameterCount, line);
    }
}