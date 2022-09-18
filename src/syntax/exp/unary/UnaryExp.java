package syntax.exp.unary;

import lexer.token.Token;

import java.util.LinkedList;

public class UnaryExp {
    private final ExpUnit unit;
    private final LinkedList<Token> ops;
    
    public UnaryExp(ExpUnit unit, LinkedList<Token> ops) {
        this.unit = unit;
        this.ops = ops;
    }
    
    public LVal getLVal() {
        if (unit instanceof PrimaryExp) {
            return ((PrimaryExp) unit).getLVal();
        }
        return null;
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
