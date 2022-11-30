package backend.instruction;

import backend.element.Reg;

public class Mult extends MIPSCode {
    private final Reg reg1;
    private final Reg reg2;
    
    public Mult(Reg reg1, Reg reg2) {
        this.reg1 = reg1;
        this.reg2 = reg2;
    }
    
    @Override
    public String toString() {
        return "mult " + reg1 + ", " + reg2 + "\n";
    }
}
