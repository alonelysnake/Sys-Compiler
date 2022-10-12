package syntax.exp.multi;

import error.AnalysisState;
import lexer.token.Ident;
import lexer.token.Token;
import lexer.token.TokenCategory;
import middle.BlockInfo;
import middle.MiddleState;
import symbol.SymTable;
import syntax.decl.BType;
import syntax.exp.unary.ExpUnit;
import syntax.exp.unary.UnaryExp;

import java.util.Iterator;
import java.util.LinkedList;

public class MulExp extends ExpList<UnaryExp> {
    public MulExp(LinkedList<Token> ops, LinkedList<UnaryExp> units) {
        super(ops, units, "<MulExp>\n");
    }
    
    public ExpUnit getFirstExpUnit() {
        if (size() != 1) {
            return null;
        }
        return getFirst().getFirstExpUnit();
    }
    
    public BType getExpType(AnalysisState state) {
        if (size() == 1) {
            return getFirst().getExpType(state);
        }
        BType type = getFirst().getExpType(state);
        for (UnaryExp unit : getUnits()) {
            BType next = unit.getExpType(state);
            if (type != next) {
                return null;
            }
        }
        if (type == BType.VOID) {
            return null;
        }
        return type;
    }
    
    public int calConst(SymTable symTable) {
        Iterator<UnaryExp> unary = getUnits().iterator();
        Iterator<Token> op = getOps().iterator();
        int ans = unary.next().calConst(symTable);
        while (unary.hasNext()) {
            TokenCategory type = op.next().getType();
            if (type.equals(TokenCategory.MULT)) {
                ans *= unary.next().calConst(symTable);
            } else if (type.equals(TokenCategory.DIV)) {
                ans /= unary.next().calConst(symTable);
            } else {
                ans %= unary.next().calConst(symTable);
            }
        }
        return ans;
    }
    
    @Override
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        getUnits().forEach(unit -> names.addAll(unit.getNames()));
        return names;
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
    }
}
