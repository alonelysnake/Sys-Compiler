package syntax.stmt.multi;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import lexer.token.TokenCategory;
import symbol.SymTable;
import syntax.BlockItem;
import syntax.func.FuncDef;
import syntax.stmt.Stmt;
import syntax.stmt.single.ReturnStmt;

import java.util.LinkedList;

public class Block implements MultiStmt {
    private final Token leftBrace;
    private final LinkedList<BlockItem> items;
    private final Token rightBrace;
    
    public Block(Token leftBrace, LinkedList<BlockItem> items, Token rightBrace) {
        this.leftBrace = leftBrace;
        this.items = items;
        this.rightBrace = rightBrace;
    }
    
    public void analyse(AnalysisState state) {
        SymTable symTable = state.getSymTable();
        //先判断是否是函数体的第一层block，只有不是才新增栈符号表(是的时候由FuncDef新增并处理了)
        if (!state.isInFunc() || state.getSymTable().getParent().getParent() != null) {
            symTable = new SymTable(symTable);
            state.pushSymTable(symTable);
        }
        // 后续的扫描处理
        for (BlockItem item : items) {
            item.analyse(state);
        }
        
        state.popSymTable();
        
        //return检查
        FuncDef func = state.getCurFunc();
        if (state.isGlobal() && (func == null || func.getType().equals(TokenCategory.INT))) {
            if (items.isEmpty() || !(items.getLast() instanceof Stmt)) {
                state.addError(new Error(rightBrace.getLine(), ErrorType.LACK_RETURN));
            } else {
                Stmt lastStmt = (Stmt) items.getLast();
                if (!(lastStmt.getSingle() instanceof ReturnStmt)) {
                    state.addError(new Error(rightBrace.getLine(), ErrorType.LACK_RETURN));
                }
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(leftBrace);
        for (BlockItem item : items) {
            sb.append(item);
        }
        sb.append(rightBrace);
        
        sb.append("<Block>\n");
        return sb.toString();
    }
}
