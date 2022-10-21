package syntax.exp.unary;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.BinaryOp;
import middle.instruction.INode;
import middle.instruction.Load;
import middle.instruction.Nop;
import middle.val.Address;
import middle.val.Number;
import middle.val.Variable;
import symbol.SymTable;
import symbol.Symbol;
import syntax.decl.BType;

import java.util.ArrayList;
import java.util.LinkedList;

public class LVal implements PrimaryUnit {
    /**
     * 左值
     * Ident + '[' + exp + ']'
     */
    
    private final Ident name;
    private final LinkedList<Dimension> dimensions;
    
    public LVal(Ident name) {
        this.name = name;
        this.dimensions = new LinkedList<>();
    }
    
    public LVal(Ident name, LinkedList<Dimension> dimensions) {
        this.name = name;
        this.dimensions = dimensions;
    }
    
    //错误处理，统计未定义变量和修改常量
    public Ident getName() {
        return name;
    }
    
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        names.addLast(name);
        if (this.dimensions != null) {
            dimensions.forEach(dim -> names.addAll(dim.getNames()));
        }
        return names;
    }
    
    public LinkedList<Dimension> getDimensions() {
        return dimensions;
    }
    
    @Override
    public int getMaxLine() {
        if (dimensions == null || dimensions.size() == 0) {
            return name.getLine();
        }
        return dimensions.getLast().getMaxLine();
    }
    
    public int getDimNum() {
        if (dimensions == null) {
            return 0;
        }
        return dimensions.size();
    }
    
    public BType getBtype(AnalysisState state) {
        Symbol symbol = state.getSymTable().get(name.getName());
        if (symbol == null) {
            System.err.println("LVal-getType()：变量初始化时有问题");
            return null;
        }
        BType initType = symbol.getType();
        if (initType == BType.MAT) {
            if (dimensions.isEmpty()) {
                return BType.MAT;
            } else if (dimensions.size() == 1) {
                return BType.ARR;
            } else if (dimensions.size() == 2) {
                return BType.INT;
            } else {
                System.err.println("LVal-getBType()：出现二维以上的数组" + dimensions.size());
                return null;
            }
        } else if (initType == BType.ARR) {
            if (dimensions.isEmpty()) {
                return BType.ARR;
            } else if (dimensions.size() == 1) {
                return BType.INT;
            } else {
                System.err.println("LVal-getBType()：出现二维以上的数组" + dimensions.size());
                return null;
            }
        } else if (initType == BType.INT) {
            return BType.INT;
        }
        System.err.println("LVal-getType()：变量初始化时有问题");
        return null;
    }
    
    public void analyse(AnalysisState state) {
        //检查变量是否存在
        if (!state.getSymTable().contains(name.getName(), true)) {
            state.addError(new Error(name.getLine(), ErrorType.UNDEFINED_IDENT));
        }
        //检查维数中的exp是否正确
        for (Dimension dim : dimensions) {
            dim.analyse(state);
        }
    }
    
    public int calConst(SymTable symTable) {
        Symbol symbol = symTable.get(name.getName());
        ArrayList<Integer> dims = new ArrayList<>();
        if (!dimensions.isEmpty()) {
            for (Dimension dimension : dimensions) {
                dims.add(dimension.calConst(symTable));
            }
        }
        return symbol.getConstVal(dims);
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        // 此处生成的是lVal在表达式中参与运算时的情况，作为真正的左值被赋值时在assign和getint中确定
        INode first = new Nop();
        INode last = first;
        Symbol symbol = state.getSymTable().get(name.getName());
        if (symbol.getType().equals(BType.INT)) {
            //TODO 是否规范为 t1 = a 的形式
            Variable var = new Variable(name.getName() + "#" + symbol.getDepth());
            return new BlockInfo(var, first, last);
        } else if (symbol.getType().equals(BType.ARR)) {
            BlockInfo addrBlock = getAddr(state);
            last = last.insert(addrBlock.getFirst());
            if (dimensions.isEmpty()) {
                //int a[10]; xxx = a 传递的是数组地址
                //TODO 是否规范为 t1 = a 的形式
                return new BlockInfo(addrBlock.getRetVal(), first, last);
            } else {
                //传递的是数
                Variable tmpVar = new Variable(String.valueOf(MiddleState.tmpCnt++));
                INode load = new Load(tmpVar, (Address) addrBlock.getRetVal());
                last = last.insert(load);
                return new BlockInfo(tmpVar, first, last);
            }
        } else if (symbol.getType().equals(BType.MAT)) {
            BlockInfo addrBlock = getAddr(state);
            last = last.insert(addrBlock.getFirst());
            if (dimensions.size() != 2) {
                //int a[10]; xxx = a 传递的是数组地址
                //TODO 是否规范为 t1 = a 或 t1 = a[0] 的形式
                return new BlockInfo(addrBlock.getRetVal(), first, last);
            } else {
                //传递的是数
                Variable tmpVar = new Variable(String.valueOf(MiddleState.tmpCnt++));
                INode load = new Load(tmpVar, (Address) addrBlock.getRetVal());
                last = last.insert(load);
                return new BlockInfo(tmpVar, first, last);
            }
        } else {
            System.err.println("lVal: 出现非法数组");
            return null;
        }
    }
    
    public BlockInfo getAddr(MiddleState state) {
        //TODO 此处注意是否*4byte
        //计算加上偏移量后的最终地址
        INode first = new Nop();
        INode last = first;
        Address ret;// 最终的地址
        Symbol symbol = state.getSymTable().get(name.getName());
        if (symbol.getType().equals(BType.ARR)) {
            if (dimensions.isEmpty()) {
                ret = new Address(name.getName() + "#" + symbol.getDepth());
            } else {
                BlockInfo offset = dimensions.get(0).generateIcode(state);
                ret = new Address(String.valueOf(MiddleState.tmpCnt++));
                Address base = new Address(name.getName() + "#" + symbol.getDepth());
                BinaryOp calAddr = new BinaryOp(ret, BinaryOp.Operator.ADD, base, offset.getRetVal());// addr = base + offset
                last = last.insert(offset.getFirst());
                last = last.insert(calAddr);
            }
        } else if (symbol.getType().equals(BType.MAT)) {
            if (dimensions.isEmpty()) {
                ret = new Address(name.getName() + "#" + symbol.getDepth());
            } else {
                BlockInfo dim1 = dimensions.get(0).generateIcode(state);
                middle.val.Number number = new Number(symbol.getDims().get(1));
                Variable tmpOffset = new Variable(String.valueOf(MiddleState.tmpCnt++));
                BinaryOp calOffset1 = new BinaryOp(tmpOffset, BinaryOp.Operator.MULT, dim1.getRetVal(), number);//第一维偏移量
                last = last.insert(dim1.getFirst());
                last = last.insert(calOffset1);
                Address base = new Address(name.getName() + "#" + symbol.getDepth());// 左值基地址
                if (dimensions.size() == 2) {
                    BlockInfo dim2 = dimensions.get(1).generateIcode(state);
                    Variable offset = new Variable(String.valueOf(MiddleState.tmpCnt++));
                    BinaryOp calOffset2 = new BinaryOp(offset, BinaryOp.Operator.ADD, tmpOffset, dim2.getRetVal());// 最终偏移量
                    last = last.insert(dim2.getFirst());
                    last = last.insert(calOffset2);
                    ret = new Address(String.valueOf(MiddleState.tmpCnt++));
                    BinaryOp calAddr = new BinaryOp(ret, BinaryOp.Operator.ADD, base, offset);// addr = base + offset
                    last = last.insert(calAddr);
                } else {
                    ret = new Address(String.valueOf(MiddleState.tmpCnt++));
                    BinaryOp calAddr = new BinaryOp(ret, BinaryOp.Operator.ADD, base, tmpOffset);// addr = base + offset
                    last = last.insert(calAddr);
                }
            }
        } else {
            System.err.println("不需要计算维度或维度过高");
            return null;
        }
        return new BlockInfo(ret, first, last);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.toString());
        for (Dimension dimension : dimensions) {
            sb.append(dimension);
        }
        return sb.append("<LVal>\n").toString();
    }
}
