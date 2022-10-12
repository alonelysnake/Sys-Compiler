package syntax.stmt;

import error.AnalysisState;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import syntax.BlockItem;
import syntax.stmt.multi.MultiStmt;
import syntax.stmt.single.SingleStmt;

public class Stmt implements BlockItem {
    public enum StmtType {
        EMPTY,//可直接优化掉？
        SINGLE,
        MULTI
    }
    
    private final SingleStmt single;
    private final MultiStmt multi;
    private final StmtType type;
    
    public Stmt(Token semicolon) {
        this.single = new SingleStmt(semicolon);
        this.multi = null;
        this.type = StmtType.EMPTY;
    }
    
    public Stmt(SingleStmt single) {
        this.single = single;
        this.multi = null;
        this.type = StmtType.SINGLE;
    }
    
    public Stmt(MultiStmt multi) {
        this.single = null;
        this.multi = multi;
        this.type = StmtType.MULTI;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        if (this.type == StmtType.MULTI) {
            multi.analyse(state);
        } else {
            single.analyse(state);
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
    }
    
    public SingleStmt getSingle() {
        return single;
    }
    
    @Override
    public String toString() {
        if (type == StmtType.MULTI) {
            return this.multi + "<Stmt>\n";
        } else {
            return this.single + "<Stmt>\n";
        }
    }
}
