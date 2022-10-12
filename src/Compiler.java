import error.AnalysisState;
import lexer.Lexer;
import lexer.token.Token;
import syntax.CompUnit;
import syntax.CompUnitParser;

import java.util.LinkedList;

public class Compiler {
    public static void main(String[] args) {
        String inputfile;
        String outputFile;
        String errFile = "error.txt";
        if (args.length > 0) {
            inputfile = args[1];
            outputFile = args[3];
        } else {
            inputfile = "testfile.txt";
            outputFile = "output.txt";
        }
        Lexer lexer = new Lexer(FileIO.readFile(inputfile));
        
        LinkedList<Token> tokens = lexer.tackle();
        
        CompUnitParser parser = new CompUnitParser(tokens);
        CompUnit unit = parser.parseCompUnit();
        //FileIO.writeParser(outputFile, unit);
        
        AnalysisState state = new AnalysisState();
        unit.analyse(state);
        
        FileIO.writeError(errFile, state.getErrorTable());
    }
}
