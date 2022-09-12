import lexer.Lexer;
import lexer.Token;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {
    public static void main(String[] args) {
        StringBuilder input = new StringBuilder();
        String inputfile = "testfile.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputfile));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {
                input.append(s);
                input.append("\n");
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Lexer lexer = new Lexer(input.toString());
        ArrayList<Token> tokens = lexer.tackle();
        String outputFile = "output.txt";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
            for (Token token : tokens) {
                bw.write(token.toString() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
