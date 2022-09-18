package syntax.exp.multi;

public class Cond {
    private LOrExp exp;
    
    public Cond(LOrExp exp) {
        this.exp = exp;
    }
    
    @Override
    public String toString() {
        return exp.toString() + "<Cond>\n";
    }
}
