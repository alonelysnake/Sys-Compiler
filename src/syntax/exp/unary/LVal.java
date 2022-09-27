package syntax.exp.unary;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;

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
        if (dimensions == null) {
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
