package syntax.exp.unary;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;
import middle.BlockInfo;
import middle.MiddleState;
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
        //TODO
        return null;
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
