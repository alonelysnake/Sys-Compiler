package syntax.func;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.FuncEntry;
import middle.instruction.INode;
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
        INode first = new FuncEntry("func_main", 0);
        state.getLabelTable().connect("func_main", first);
        INode last = first;
        
        state.inBlock();
        
        last = last.insert(content.generateIcode(state).getFirst());
        
        state.outBlock();
        return new BlockInfo(null, first, last);
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
