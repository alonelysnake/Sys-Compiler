package syntax.decl;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;
import lexer.token.Token;
import symbol.SymTable;
import symbol.Symbol;
import syntax.SyntaxNode;
import syntax.exp.unary.Dimension;

import java.util.LinkedList;

public class Def implements SyntaxNode {
    private final Ident name;
    private final LinkedList<Dimension> dimensions;
    
    private final Token assign;
    private final InitVal val;
    
    private final boolean constFlag;
    
    //无初值的定义
    public Def(Ident name, LinkedList<Dimension> dimensions) {
        this.name = name;
        this.dimensions = dimensions;
        this.assign = null;
        this.val = null;
        this.constFlag = false;
    }
    
    //有初值的定义
    public Def(Ident name, LinkedList<Dimension> dimensions,
               Token assign, InitVal val, boolean constFlag) {
        this.name = name;
        this.dimensions = dimensions;
        this.assign = assign;
        this.val = val;
        this.constFlag = constFlag;
    }
    
    public boolean isInit() {
        return this.val != null;
    }
    
    public Ident getName() {
        return name;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        SymTable symTable = state.getSymTable();
        if (symTable.contains(name.getName(), false)) {
            symTable.add(new Symbol(name.getName(), constFlag));
        } else {
            state.addError(new Error(name.getLine(), ErrorType.REDEFINED_IDENT));
        }
        dimensions.forEach(dim -> dim.analyse(state));
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        dimensions.forEach(sb::append);
        if (isInit()) {
            sb.append(assign);
            sb.append(val);
        }
        if (constFlag) {
            sb.append("<ConstDef>\n");
        } else {
            sb.append("<VarDef>\n");
        }
        return sb.toString();
    }
}
