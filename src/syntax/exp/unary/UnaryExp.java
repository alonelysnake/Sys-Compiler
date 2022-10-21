package syntax.exp.unary;

import error.AnalysisState;
import lexer.token.Ident;
import lexer.token.Token;
import lexer.token.TokenCategory;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.INode;
import middle.instruction.UnaryOp;
import middle.val.Value;
import middle.val.Variable;
import symbol.SymTable;
import syntax.SyntaxNode;
import syntax.decl.BType;
import syntax.func.FuncDef;

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
                //TODO 检查符号表直接得到维度
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
                return ((LVal) unit).getBtype(state);
            } else {
                return ((SubExp) unit).getExp().getExpType(state);
            }
        }
    }
    
    @Override
    public void analyse(AnalysisState state) {
        this.unit.analyse(state);
    }
    
    public int calConst(SymTable symTable) {
        if (unit instanceof FuncCall) {
            System.err.println("常量出现funccall");
            return 0;
        }
        PrimaryExp exp = (PrimaryExp) unit;
        return exp.calConst(symTable);
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO 遍历符号，考虑消除-+-等形式
        BlockInfo unitBlock = unit.generateIcode(state);
        INode first = unitBlock.getFirst();
        INode last = unitBlock.getLast();
        Value val = unitBlock.getRetVal();
        for (int i = ops.size() - 1; i >= 0; i--) {
            Variable newVal = new Variable(String.valueOf(MiddleState.tmpCnt++));
            INode unaryOp;
            switch (ops.get(i).getType()) {
                case PLUS:
                    MiddleState.tmpCnt--;
                    break;
                case MINUS:
                    unaryOp = new UnaryOp(val, UnaryOp.Operator.NEG, newVal);
                    last = last.insert(unaryOp);
                    val = newVal;
                    break;
                case NOT:
                    unaryOp = new UnaryOp(val, UnaryOp.Operator.NOT, newVal);
                    last = last.insert(unaryOp);
                    val = newVal;
                    break;
            }
        }
        return new BlockInfo(val, first, last);
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
