package syntax.func;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import syntax.SyntaxNode;
import syntax.stmt.multi.Block;

public class MainFunc implements SyntaxNode {
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
    public void analyse(AnalysisState state) {
        //TODO 主函数不需要判断缺少')'?
        if (rightParent == null) {
            state.addError(new Error(leftParent.getLine(), ErrorType.LACK_R_PARENT));
        }
        content.analyse(state);
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
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
