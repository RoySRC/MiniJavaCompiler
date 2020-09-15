package VaporIR.HelperVariableContainers;

import java.util.Stack;

public class MsgSendVars {

    private final Stack<Function> sendExpressionStack = new Stack<>();

    public Stack<Function> FunctionExpressionStack() {
        return sendExpressionStack;
    }
}
