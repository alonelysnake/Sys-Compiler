package backend.instruction;

import backend.element.Address;
import backend.element.Reg;

public class La extends MIPSCode {
    private final Reg dst;
    private final Address addr;
    
    public La(Reg dst, Address addr) {
        this.dst = dst;
        this.addr = addr;
    }
    
    @Override
    public String toString() {
        return "la " + dst + ", " + addr + "\n";
    }
}
