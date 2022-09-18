package syntax.func;

import lexer.token.Token;
import syntax.stmt.multi.Block;

public class MainFunc {
    private final Token intSym;
    private final Token mainSym;
    private final Token leftParent;
    private final Token rightParent;
    private final Block content;
    
    public MainFunc(Token intSym, Token mainSym, Token leftParent, Token rightParent,
                    Block content) {
        this.intSym = intSym;
        this.mainSym = mainSym;
        this.leftParent = leftParent;
        this.rightParent = rightParent;
        this.content = content;
    }
    
    @Override
    public String toString() {
        return String.valueOf(intSym) +
                mainSym +
                leftParent +
                rightParent +
                content +
                "<MainFuncDef>\n";
    }
}
