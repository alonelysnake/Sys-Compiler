package backend.instruction;

import backend.element.Imm;
import backend.element.Reg;

public class Li extends MIPSCode {
    private final Reg dst;
    private final Imm imm;
    
    public Li(Reg dst, Imm imm) {
        this.dst = dst;
        this.imm = imm;
    }
    
    @Override
    public String toString() {
        return "li " + dst + ", " + imm + "\n";
    }
}
