package syntax.exp.multi;

import error.AnalysisState;
import lexer.token.Ident;
import middle.BlockInfo;
import middle.MiddleState;
import symbol.SymTable;
import syntax.SyntaxNode;
import syntax.decl.BType;
import syntax.exp.unary.ExpUnit;

import java.util.LinkedList;

public class Exp implements SyntaxNode {
    private final AddExp exp;
    
    public Exp(AddExp exp) {
        this.exp = exp;
    }
    
    public AddExp getExp() {
        return exp;
    }
    
    public ExpUnit getFirstExpUnit() {
        return exp.getFirstExpUnit();
    }
    
    public BType getExpType(AnalysisState state) {
        return exp.getExpType(state);
    }
    
    public LinkedList<Ident> getNames() {
        return exp.getNames();
    }
    
    public int getMaxLine() {
        return exp.getMaxLine();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        exp.analyse(state);
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
    }
    
    public int calConst(SymTable table) {
        return exp.calConst(table);
    }
    
    @Override
    public String toString() {
        return exp.toString() + "<Exp>\n";
    }
}
