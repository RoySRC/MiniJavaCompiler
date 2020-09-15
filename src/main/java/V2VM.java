import VaporMIR.VaporMVisitor;
import cs132.util.ProblemException;
import cs132.vapor.parser.VaporParser;
import cs132.vapor.ast.VaporProgram;
import cs132.vapor.ast.VBuiltIn.Op;

import core.util.LOGGER;

import java.io.*;

public class V2VM {
    private static final transient LOGGER log = new LOGGER(V2VM.class.getSimpleName(), true);

    public static VaporProgram parseVapor(InputStream in, PrintStream err) throws IOException {
        Op[] ops = {
            Op.Add, Op.Sub, Op.MulS, Op.Eq, Op.Lt, Op.LtS,
            Op.PrintIntS, Op.HeapAllocZ, Op.Error,
        };
        boolean allowLocals = true;
        String[] registers = null;
        boolean allowStack = false;

        VaporProgram program;
        try {
            program = VaporParser.run(new InputStreamReader(in), 1, 1,
                java.util.Arrays.asList(ops),
                allowLocals, registers, allowStack);
        }
        catch (ProblemException ex) {
            err.println(ex.getMessage());
            return null;
        }

        return program;
    }

    public static InputStream generateCode(InputStream outputStream) throws Throwable {
        log.info("Converting to VaporM IR");
        VaporMVisitor vmv = new VaporMVisitor(parseVapor(outputStream, System.err));
        log.info("Conversion to VaporM IR successful");
        outputStream.close();
        return new ByteArrayInputStream(vmv.vaporMProgram.getByteArray());
    }

    public static void main(String[] args) throws Throwable {
        log.info("Starting V2VM...");
        VaporMVisitor vmv = new VaporMVisitor(parseVapor(System.in, System.err));
        vmv.vaporMProgram.print();
        log.info("Done V2VM...");
    }

}
