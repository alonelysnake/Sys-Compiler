package backend.instruction;

import backend.element.LabelAddr;
import backend.element.MIPSUnit;

public class Bne extends MIPSCode {
    private final MIPSUnit operand1;
    private final MIPSUnit operand2;
    private final LabelAddr label;
    
    public Bne(MIPSUnit operand1, MIPSUnit operand2, LabelAddr label) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.label = label;
    }
    
    @Override
    public String toString() {
        return "bne " + operand1 + ", " + operand2 + ", " + label + "\n";
    }
}
