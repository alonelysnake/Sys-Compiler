package syntax.func;

import error.AnalysisState;
import error.Error;
import error.ErrorType;
import lexer.token.Ident;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.FetchParam;
import middle.instruction.INode;
import middle.val.Address;
import middle.val.Variable;
import symbol.SymTable;
import symbol.Symbol;
import syntax.SyntaxNode;
import syntax.decl.BType;
import syntax.exp.unary.Dimension;

import java.util.ArrayList;
import java.util.LinkedList;

public class FuncFParam implements SyntaxNode {
    private final Token type;//固定为int
    private final Ident name;
    
    private final Dimension firstDimension;
    private final LinkedList<Dimension> followDimensions;
    
    public FuncFParam(Token type, Ident name) {
        this.type = type;
        this.name = name;
        this.firstDimension = null;
        this.followDimensions = null;
    }
    
    public FuncFParam(Token type, Ident name, Dimension firstDimension) {
        this.type = type;
        this.name = name;
        this.firstDimension = firstDimension;
        this.followDimensions = null;
    }
    
    public FuncFParam(Token type, Ident name,
                      Dimension firstDimension, LinkedList<Dimension> followDimensions) {
        this.type = type;
        this.name = name;
        this.firstDimension = firstDimension;
        this.followDimensions = followDimensions;
    }
    
    public Ident getName() {
        return name;
    }
    
    public BType getType() {
        if (firstDimension == null) {
            return BType.INT;
        }
        if (followDimensions == null) {
            return BType.ARR;
        }
        if (followDimensions.size() > 1) {
            System.err.println("FuncFParam-getType()：数组维度大于2");
            return null;
        }
        return BType.MAT;
    }
    
    public int getDimNum() {
        if (firstDimension == null) {
            return 0;
        }
        if (followDimensions == null) {
            return 1;
        }
        return 1 + followDimensions.size();
    }
    
    public int getMaxLine() {
        if (firstDimension == null) {
            return name.getLine();
        } else if (followDimensions == null) {
            return firstDimension.getMaxLine();
        }
        return followDimensions.getLast().getMaxLine();
    }
    
    @Override
    public void analyse(AnalysisState state) {
        SymTable symTable = state.getSymTable();
        if (symTable.contains(name.getName(), false)) {
            state.addError(new Error(name.getLine(), ErrorType.REDEFINED_IDENT));
        } else {
            symTable.add(new Symbol(name.getName(), false, getDimNum()));
        }
        if (firstDimension != null) {
            firstDimension.analyse(state);
        }
        if (followDimensions != null) {
            for (Dimension dim : followDimensions) {
                dim.analyse(state);
            }
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        ArrayList<Integer> dimLen = new ArrayList<>();
        int size = getDimNum();
        Symbol symbol = new Symbol(name.getName(), false, getDimNum());
        dimLen.add(0);
        if (followDimensions != null) {
            dimLen.add(followDimensions.get(0).calConst(state.getSymTable()));
        }
        symbol.setInit(dimLen, new ArrayList<>());
        state.getSymTable().add(symbol);
        INode fetch;
        if (size == 0) {
            Variable var = new Variable(name.getName() + "#" + symbol.getDepth());
            fetch = new FetchParam(var);
        } else {
            Address addr = new Address(name.getName() + "#" + symbol.getDepth());
            fetch = new FetchParam(addr);
        }
        return new BlockInfo(null, fetch, fetch);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(name);
        if (this.firstDimension != null) {
            sb.append(firstDimension);
            if (this.followDimensions != null) {
                this.followDimensions.forEach(sb::append);
            }
        }
        sb.append("<FuncFParam>\n");
        return sb.toString();
    }
}
