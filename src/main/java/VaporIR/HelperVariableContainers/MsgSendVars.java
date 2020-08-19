package VaporIR.HelperVariableContainers;

import java.util.Stack;

public class MsgSendVars {

    private Stack<Function> sendExpressionStack = new Stack<>();
    private Stack<String> primaryExpressionTypeStack = new Stack<>();
    private int FIRST = -1; // will be set to true by the first message send function call

    public Stack<Function> FunctionExpressionStack() {
        return sendExpressionStack;
    }
}
