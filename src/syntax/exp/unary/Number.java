package syntax.exp.unary;

import lexer.token.IntConst;

public class Number implements PrimaryUnit {
    private final IntConst strVal;
    
    public Number(IntConst strVal) {
        this.strVal = strVal;
    }
    
    @Override
    public String toString() {
        return strVal + "<Number>\n";
    }
}
