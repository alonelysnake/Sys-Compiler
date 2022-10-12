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

import java.util.Iterator;
import java.util.LinkedList;

public class AddExp extends ExpList<MulExp> {
    public AddExp(LinkedList<Token> ops, LinkedList<MulExp> units) {
        super(ops, units, "<AddExp>\n");
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
        for (MulExp unit : getUnits()) {
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
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
    }
    
    public int calConst(SymTable symTable) {
        Iterator<MulExp> mult = getUnits().iterator();
        Iterator<Token> op = getOps().iterator();
        int ans = mult.next().calConst(symTable);
        while (mult.hasNext()) {
            if (op.next().getType().equals(TokenCategory.PLUS)) {
                ans += mult.next().calConst(symTable);
            } else {
                ans -= mult.next().calConst(symTable);
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
}
