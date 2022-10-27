package backend.instruction;

public class Jal extends MIPSCode {
    private final String label;
    
    public Jal(String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return "jal " + label + "\n";
    }
}
