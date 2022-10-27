package syntax.stmt.single;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.INode;
import middle.instruction.Input;
import middle.instruction.Move;
import middle.instruction.Return;
import middle.instruction.Save;
import middle.val.Address;
import middle.val.Variable;
import symbol.SymTable;
import symbol.Symbol;
import syntax.decl.BType;
import syntax.exp.unary.LVal;

public class InputStmt extends SingleStmt {
    private final LVal lVal;
    private final Token assign;
    private final Token name;
    private final Token leftParent;
    private final Token rightParent;
    
    public InputStmt(LVal lVal, Token assign,
                     Token name, Token leftParent, Token rightParent, Token semicolon) {
        super(semicolon);
        this.lVal = lVal;
        this.assign = assign;
        this.name = name;
        this.leftParent = leftParent;
        this.rightParent = rightParent;
    }
    
    public LVal getlVal() {
        return lVal;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        SymTable symTable = state.getSymTable();
        // 左值修改
        String name = lVal.getName().getName();
        Symbol symbol = symTable.get(name);
        if (symbol != null) {
            if (symbol.isConst()) {
                state.addError(new Error(lVal.getName().getLine(), ErrorType.MODIFY_CONST));
            }
        }
        lVal.analyse(state);
        //右括号检查
        if (rightParent == null) {
            state.addError(new Error(leftParent.getLine(), ErrorType.LACK_R_PARENT));
        }
        //分号检查
        if (!hasSemicolon()) {
            if (rightParent == null) {
                state.addError(new Error(leftParent.getLine(), ErrorType.LACK_SEMICOLON));
            } else {
                state.addError(new Error(rightParent.getLine(), ErrorType.LACK_SEMICOLON));
            }
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        INode first = new Input();
        INode last = first;
        Variable retReg = new Variable(Return.RET_REG);//右值
        SymTable symTable = state.getSymTable();
        Symbol leftSymbol = symTable.get(this.lVal.getName().getName());
        if (leftSymbol.getType().equals(BType.INT)) {
            // 对非数组变量赋值
            Variable var;
            if (leftSymbol.isGlobal()) {
                var = new Variable("global_" + leftSymbol.getName() + "#" + leftSymbol.getDepth());
            } else {
                var = new Variable(leftSymbol.getName() + "#" + leftSymbol.getDepth());
            }
            INode assignNode = new Move(var, retReg);
            last = last.insert(assignNode);
        } else if (leftSymbol.getType().equals(BType.ARR) ||
                leftSymbol.getType().equals(BType.MAT)) {
            // 对一维数组a[0]或二维数组a[0][0]赋值
            BlockInfo addr = lVal.getAddr(state);
            Save save = new Save((Address) addr.getRetVal(), retReg);
            last = last.insert(addr.getFirst());
            last = last.insert(save);
        } else {
            System.err.println("input赋值数组维度过高");
        }
        return new BlockInfo(null, first, last);
    }
    
    @Override
    public String toString() {
        return lVal.toString() +
                assign +
                name +
                leftParent +
                rightParent +
                getSemicolon();
    }
}
