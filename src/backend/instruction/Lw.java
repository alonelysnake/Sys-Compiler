package backend.instruction;

import backend.element.Address;
import backend.element.Reg;

public class Lw extends MIPSCode {
    private final Reg dst;
    private final Address addr;
    
    public Lw(Reg dst, Address addr) {
        this.dst = dst;
        this.addr = addr;
    }
    
    @Override
    public String toString() {
        return "lw " + dst + ", " + addr + "\n";
    }
}
