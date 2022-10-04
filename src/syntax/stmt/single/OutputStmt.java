package syntax.stmt.single;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.FormatString;
import lexer.token.Token;
import syntax.exp.multi.Exp;

import java.util.Iterator;
import java.util.LinkedList;

public class OutputStmt extends SingleStmt {
    private final Token printf;
    private final Token leftParent;
    private final FormatString str;
    private final LinkedList<Token> commas;
    private final LinkedList<Exp> paras;
    private final Token rightParent;
    
    public OutputStmt(Token printf, Token leftParent, FormatString str,
                      LinkedList<Token> commas, LinkedList<Exp> paras,
                      Token rightParent, Token semicolon) {
        super(semicolon);
        this.printf = printf;
        this.leftParent = leftParent;
        this.str = str;
        this.commas = commas;
        this.paras = paras;
        this.rightParent = rightParent;
    }
    
    //获取输出语句中不包含'"'的格式化字符串实际内容
    public String getFormatStr() {
        return str.getContent();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        //分析str
        String format = str.getContent();
        int paraNum = 0;
        for (int i = 0; i < format.length(); i++) {
            char c = format.charAt(i);
            if (c == 32 || c == 33 || (c >= 40 && c <= 126)) {
                if (c == '\\') {
                    if (i + 1 < format.length() && format.charAt(i + 1) == 'n') {
                        continue;
                    }
                } else {
                    continue;
                }
            } else if (c == '%') {
                if (i + 1 < format.length() && format.charAt(i + 1) == 'd') {
                    paraNum++;
                    continue;
                }
            }
            state.addError(new Error(str.getLine(), ErrorType.ILLEGAL_CHAR));
            break;
        }
        //分析每个para内部的正确性
        for (Exp exp : paras) {
            exp.analyse(state);
        }
        //分析参数个数匹配
        if (paraNum != paras.size()) {
            state.addError(new Error(printf.getLine(), ErrorType.MISMATCH_PRINTF));
        }
        //分析右括号
        int maxLine = str.getLine();
        if (rightParent == null) {
            if (paraNum != 0) {
                maxLine = paras.getLast().getMaxLine();
            }
            state.addError(new Error(maxLine, ErrorType.LACK_R_PARENT));
        }
        //分析分号
        if (!hasSemicolon()) {
            if (rightParent == null) {
                state.addError(new Error(maxLine, ErrorType.LACK_SEMICOLON));
            } else {
                state.addError(new Error(rightParent.getLine(), ErrorType.LACK_SEMICOLON));
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(printf);
        sb.append(leftParent);
        sb.append(str);
        Iterator<Token> commaIterator = commas.iterator();
        Iterator<Exp> paraIterator = paras.iterator();
        while (commaIterator.hasNext() && paraIterator.hasNext()) {
            sb.append(commaIterator.next());
            sb.append(paraIterator.next());
        }
        sb.append(rightParent);
        sb.append(getSemicolon());
        return sb.toString();
    }
}
