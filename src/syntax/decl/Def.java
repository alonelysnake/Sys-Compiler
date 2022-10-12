package syntax.decl;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
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
    
    public int getMaxLine() {
        if (val == null) {
            if (dimensions.isEmpty()) {
                return name.getLine();
            }
            return dimensions.getLast().getMaxLine();
        }
        return val.getMaxLine();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        SymTable symTable = state.getSymTable();
        boolean hasSame = false;
        if (symTable.contains(name.getName(), false)) {
            state.addError(new Error(name.getLine(), ErrorType.REDEFINED_IDENT));
            hasSame = true;
        } /*else {
            symTable.add(new Symbol(name.getName(), constFlag, dimensions.size()));
        }*/
        dimensions.forEach(dim -> dim.analyse(state));
        if (val != null) {
            val.analyse(state);
        }
        // 防止出现 int a = a; 的情况
        Symbol symbol = new Symbol(name.getName(), constFlag, dimensions.size());
        //测试用，删了
//        ArrayList<Integer> dimLen = new ArrayList<>();
//        for (Dimension dim : dimensions) {
//            dimLen.add(dim.calConst(symTable));
//        }
//        ArrayList<Exp> inits = val.getInitVals();
//        symbol.setInit(dimLen, inits);
//        if (constFlag) {
//            ArrayList<Integer> cons = new ArrayList<>();
//            for (Exp init : inits) {
//                int con = init.calConst(symTable);
//                cons.add(con);
//            }
//            System.err.println("常量" + name.getName() + "值为：" + cons);
//            System.err.println("常量" + name.getName() + "维度为：" + dimLen);
//            symbol.setConstVals(cons);
//        }
        //删除结束
        if (!hasSame) {
            symTable.add(symbol);
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO 注意简化dimension
        return null;
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
