package TypeChecking.MessagePrinter;

/**
 * Interface for printing the results of typechecking. If the result of typechecking is true, then print SUCCESS else
 * print FAILURE.
 */

public class MessagePrinter {

  public static String SUCCESS = "Program type checked successfully\n";
  public static String FAILURE = "Type error\n";

  public static void printSuccess() {
    System.out.print(MessagePrinter.SUCCESS);
  }

  public static void printFailure() {
    System.out.print(MessagePrinter.FAILURE);
  }

}
