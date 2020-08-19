import TypeChecking.MessagePrinter.MessagePrinter;
import core.util.LOGGER;

import java.io.*;

public class MiniJavaCompiler {
  MiniJavaCompiler() {}

  // for logging
  private static transient final LOGGER log = new LOGGER(MiniJavaCompiler.class.getSimpleName());

  public static String filename;
  public static String output_file;

  public static boolean typeCheck() throws FileNotFoundException {
    Typecheck typecheck = new Typecheck(new FileInputStream(new File(filename)));
    return typecheck.check();
  }

  public static void main(String[] args) throws Throwable {
    for (int i=0; i<args.length; ++i) {
      String arg = args[i];
      switch (arg) {
        case "--file":
          filename = args[i+1];
          break;

        case "--o":
          output_file = args[i+1];
          break;

        case "--verbose":
          log.enableGlobalLogging();
          break;

        default:
          break;
      }
    }

    boolean programTypeChecks = MiniJavaCompiler.typeCheck();

    if (programTypeChecks) {
      log.info("Program type checked successfully.");

      InputStream outputStream = J2V.generateCode(filename);
      outputStream = V2VM.generateCode(outputStream);
      outputStream = VM2M.generateCode(outputStream);

      FileOutputStream fos = new FileOutputStream(new File(output_file));
      fos.write(outputStream.readAllBytes());
      fos.close();

    } else {
      MessagePrinter.printFailure();
    }
  }
}