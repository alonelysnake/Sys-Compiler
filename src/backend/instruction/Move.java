package backend.instruction;

import backend.element.Reg;

public class Move extends MIPSCode {
    private final Reg dst;
    private final Reg src;
    
    public Move(Reg dst, Reg src) {
        this.dst = dst;
        this.src = src;
    }
    
    @Override
    public String toString() {
        return "move " + dst + ", " + src + '\n';
    }
}
