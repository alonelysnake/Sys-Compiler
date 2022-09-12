import lexer.Lexer;
import lexer.token.Token;

import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
        String inputfile = "testfile.txt";
        Lexer lexer = new Lexer(FileIO.readFile(inputfile));
        
        ArrayList<Token> tokens = lexer.tackle();
        
        String outputFile = "output.txt";
        FileIO.writeLexer(outputFile, tokens);
        
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
