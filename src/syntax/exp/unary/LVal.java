package syntax.exp.unary;

import lexer.token.Ident;

import java.util.LinkedList;

public class LVal implements PrimaryUnit {
    /**
     * 左值
     * Ident + '[' + exp + ']'
     */
    
    private Ident name;
    private LinkedList<Dimension> dimensions;
    
    public LVal(Ident name) {
        this.name = name;
        this.dimensions = new LinkedList<>();
    }
    
    public LVal(Ident name, LinkedList<Dimension> dimensions) {
        this.name = name;
        this.dimensions = dimensions;
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
