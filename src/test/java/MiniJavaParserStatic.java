import core.MiniJavaParser;

import java.io.InputStream;

public class MiniJavaParserStatic {
  private static MiniJavaParser parser;

  public static MiniJavaParser getParser(InputStream iStream) {
    if (parser == null) parser = new MiniJavaParser(iStream);
    else MiniJavaParser.ReInit(iStream);
    return parser;
  }
}
