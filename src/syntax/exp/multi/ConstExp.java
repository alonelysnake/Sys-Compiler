package syntax.exp.multi;

public class ConstExp extends Exp {
    public ConstExp(AddExp exp) {
        super(exp);
    }
    
    @Override
    public String toString() {
        return getExp().toString() + "<ConstExp>\n";
    }
}
