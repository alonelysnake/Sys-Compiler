package backend.instruction;

import backend.element.Reg;

public class Mfhi extends MIPSCode {
    private final Reg reg;
    
    public Mfhi(Reg reg) {
        this.reg = reg;
    }
    
    @Override
    public String toString() {
        return "mfhi " + reg + "\n";
    }
}
