package backend.instruction;

import backend.element.Reg;

public class Jr extends MIPSCode {
    private final Reg ret;
    
    public Jr() {
        ret = Reg.RET_ADDR;
    }
    
    public Jr(Reg ret) {
        this.ret = ret;
    }
    
    @Override
    public String toString() {
        return "jr " + ret + "\n";
    }
}
