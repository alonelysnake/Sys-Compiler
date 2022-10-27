package backend.instruction;

public class J extends MIPSCode {
    private final String label;
    
    public J(String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return "j " + label + "\n";
    }
}
