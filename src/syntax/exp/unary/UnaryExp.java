package syntax.exp.unary;

import error.AnalysisState;
import lexer.token.Ident;
import lexer.token.Token;
import lexer.token.TokenCategory;
import syntax.SyntaxNode;
import syntax.decl.BType;
import syntax.exp.multi.MulExp;
import syntax.func.FuncDef;
import syntax.func.FuncType;

import java.util.LinkedList;

public class UnaryExp implements SyntaxNode {
    private final ExpUnit unit;
    private final LinkedList<Token> ops;
    
    public UnaryExp(ExpUnit unit, LinkedList<Token> ops) {
        this.unit = unit;
        this.ops = ops;
    }
    
    public ExpUnit getFirstExpUnit() {
        if (unit instanceof PrimaryExp && ((PrimaryExp) unit).isSubExp()) {
            return ((PrimaryExp) unit).getFirstExpUnit();
        }
        return unit;
    }
    
    public LinkedList<Ident> getNames() {
        return unit.getNames();
    }
    
    public int getMaxLine() {
        return unit.getMaxLine();
    }
    
    public BType getExpType(AnalysisState state) {
        if (unit instanceof FuncCall) {
            FuncCall call = (FuncCall) unit;
            if (state.containsFunc(call.getFuncName())) {
                FuncDef def = state.getFunc(call.getFuncName());
                if (def.getType().equals(TokenCategory.INT)) {
                    return BType.INT;
                } else {
                    return BType.VOID;
                }
            } else {
                return null;
            }
        } else {
            PrimaryUnit unit = ((PrimaryExp) this.unit).getUnit();
            if (unit instanceof Number) {
                return BType.INT;
            } else if (unit instanceof LVal) {
                return ((LVal) unit).getBtype();
            } else {
                return ((SubExp) unit).getExp().getExpType(state);
            }
        }
    }
    
    @Override
    public void analyse(AnalysisState state) {
        this.unit.analyse(state);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Token token : ops) {
            sb.append(token);
            sb.append("<UnaryOp>\n");
        }
        sb.append(unit);
        for (int i = 0; i <= ops.size(); i++) {
            sb.append("<UnaryExp>\n");
        }
        return sb.toString();
    }
}
