package syntax.stmt.single;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.INode;
import middle.instruction.Move;
import middle.instruction.Save;
import middle.val.Address;
import middle.val.Variable;
import symbol.SymTable;
import symbol.Symbol;
import syntax.decl.BType;
import syntax.exp.multi.Exp;
import syntax.exp.unary.LVal;

public class AssignStmt extends SingleStmt {
    private final LVal lval;
    private final Token assign;
    private final Exp exp;
    
    public AssignStmt(LVal lval, Token assign, Exp exp, Token semicolon) {
        super(semicolon);
        this.lval = lval;
        this.assign = assign;
        this.exp = exp;
    }
    
    public LVal getLval() {
        return lval;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        SymTable symTable = state.getSymTable();
        // 左值修改
        String name = lval.getName().getName();
        Symbol symbol = symTable.get(name);
        if (symbol != null) {
            if (symbol.isConst()) {
                state.addError(new Error(lval.getName().getLine(), ErrorType.MODIFY_CONST));
            }
        }
        lval.analyse(state);
        exp.analyse(state);
        if (!hasSemicolon()) {
            state.addError(new Error(exp.getMaxLine(), ErrorType.LACK_SEMICOLON));
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        SymTable symTable = state.getSymTable();
        Symbol leftSymbol = symTable.get(this.lval.getName().getName());
        BlockInfo rVal = exp.generateIcode(state);
        final INode first = rVal.getFirst();
        INode last = first;// 先计算右值
        if (leftSymbol.getType().equals(BType.INT)) {
            // 对非数组变量赋值
            Variable var = new Variable(leftSymbol.getName() + "#" + leftSymbol.getDepth());
            INode assignNode = new Move(var, rVal.getRetVal());
            last = last.insert(assignNode);
        } else if (leftSymbol.getType().equals(BType.ARR) ||
                leftSymbol.getType().equals(BType.MAT)) {
            // 对一维数组a[0]或二维数组a[0][0]赋值
            BlockInfo addr = lval.getAddr(state);
            Save save = new Save((Address) addr.getRetVal(), rVal.getRetVal());
            last = last.insert(addr.getFirst());
            last = last.insert(save);
        } else {
            System.err.println("input赋值数组维度过高");
        }
        return new BlockInfo(null, first, last);
    }
    
    @Override
    public String toString() {
        return lval.toString() + assign.toString() + exp.toString() + super.toString();
    }
}
