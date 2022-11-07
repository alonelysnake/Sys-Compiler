import backend.instruction.MIPSCode;
import error.Error;
import error.ErrorTable;
import lexer.token.Token;
import middle.LabelTable;
import middle.instruction.INode;
import syntax.CompUnit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class FileIO {
    /**
     * 读写文件
     */
    
    public static String readFile(String filepath) {
        StringBuilder ans = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String s;
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
    
    public static void writeLexer(String filepath, LinkedList<Token> tokens) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
            for (Token token : tokens) {
                bw.write(token.toString());
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeParser(String filepath, CompUnit compUnit) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
            bw.write(compUnit.toString());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeError(String filepath, ErrorTable table) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
            for (Error error : table.getErrors()) {
                bw.write(error.toString());
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeIR(String filepath, INode first, LabelTable labelTable) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
            while (first != null) {
                ArrayList<String> labels = labelTable.getLabels(first);
                if (labels != null) {
                    for (String l : labels) {
                        bw.write(l + ":\n");
                    }
                }
                bw.write(first.toString());
                bw.write("\n");
                first = first.getNext();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeMIPS(String filepath, MIPSCode first) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
            while (first != null) {
                bw.write(first.toString());
                first = first.getNext();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeMIPS(String filepath, String str) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
            bw.write(str);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
