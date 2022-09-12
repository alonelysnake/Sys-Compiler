import lexer.token.Token;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileIO {
    /**
     * 读写文件
     */
    
    public static String readFile(String filepath) {
        StringBuilder ans = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {
                ans.append(s);
                ans.append("\n");
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ans.toString();
    }
    
    public static void writeLexer(String filepath, ArrayList<Token> tokens) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
            for (Token token : tokens) {
                bw.write(token.toString() + "\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
