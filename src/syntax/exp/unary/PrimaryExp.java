package syntax.exp.unary;

public class PrimaryExp implements ExpUnit {
    private PrimaryUnit unit;
    
    public PrimaryExp(PrimaryUnit unit) {
        this.unit = unit;
    }
    
    public LVal getLVal() {
        if (unit instanceof LVal) {
            return (LVal) unit;
        }
        return null;
    }
    
    @Override
    public String toString() {
        return unit.toString() + "<PrimaryExp>\n";
    }
}
