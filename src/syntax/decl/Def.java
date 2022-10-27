package syntax.decl;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;
import lexer.token.IntConst;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.Definition;
import middle.instruction.INode;
import middle.instruction.Nop;
import middle.val.Address;
import middle.val.Value;
import middle.val.Variable;
import symbol.SymTable;
import symbol.Symbol;
import syntax.SyntaxNode;
import syntax.exp.multi.Exp;
import syntax.exp.unary.Dimension;
import syntax.exp.unary.Number;

import java.util.ArrayList;
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
        SymTable table = state.getSymTable();
        Symbol symbol = new Symbol(name.getName(), constFlag, dimensions.size());
        ArrayList<Integer> dimLen = new ArrayList<>();//符号维度
        for (Dimension dim : dimensions) {
            dimLen.add(dim.calConst(table));
        }
        ArrayList<Exp> initVals = new ArrayList<>();//符号初值
        if (val != null) {
            initVals = val.getInitVals();
        }
        symbol.setInit(dimLen, initVals);//设置符号的初值和长度
        symbol.setGlobal(state.isGlobal());
        Definition def;
        ArrayList<Value> middleInitVals = new ArrayList<>();
        INode last = new Nop();
        final INode first = last;
        //常量或变量的初值部分的中间代码
        // TODO 是否可以考虑递归下降到 initVal 里处理?
        if (constFlag || state.isGlobal()) {
            //全局变量和常量都应该有常量初值
            ArrayList<Integer> constVals = new ArrayList<>();
            for (Exp exp : initVals) {
                int cons = exp.calConst(table);
                Number number = new Number(new IntConst(String.valueOf(cons), -1));//临时创建用
                BlockInfo constLine = number.generateIcode(state);
                last = last.insert(constLine.getFirst());
                constVals.add(cons);
                middleInitVals.add(constLine.getRetVal());
            }
            symbol.setConstVals(constVals);//设置常量的初值
        } else {
            for (Exp exp : initVals) {
                BlockInfo expBlock = exp.generateIcode(state);
                last = last.insert(expBlock.getFirst());
                middleInitVals.add(expBlock.getRetVal());
            }
        }
        table.add(symbol);// 最后添加新定义的变量
        //符号表相关处理完成，进行最后的中间代码处理
        if (dimensions.size() == 0) {
            Variable var;
            if (symbol.isGlobal()) {
                var = new Variable("global_" + name.getName() + "#" + symbol.getDepth());
            } else {
                var = new Variable(name.getName() + "#" + symbol.getDepth());
            }
            def = new Definition(state.isGlobal(), constFlag, var, 1, middleInitVals);
        } else {
            int size = 1;
            for (int len : dimLen) {
                size *= len;
            }
            Address addr;
            if (symbol.isGlobal()) {
                addr = new Address("global_" + name.getName() + "#" + symbol.getDepth());
            } else {
                addr = new Address(name.getName() + "#" + symbol.getDepth());
            }
            def = new Definition(state.isGlobal(), constFlag, addr, size, middleInitVals);
        }
        last = last.insert(def);
        
        return new BlockInfo(null, first, last);
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
