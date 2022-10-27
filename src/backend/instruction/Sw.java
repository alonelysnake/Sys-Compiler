package backend.instruction;

import backend.element.Address;
import backend.element.Reg;

public class Sw extends MIPSCode {
    private final Reg src;
    private final Address addr;
    
    public Sw(Reg src, Address addr) {
        this.src = src;
        this.addr = addr;
    }
    
    @Override
    public String toString() {
        return "sw " + src + ", " + addr + "\n";
    }
}
