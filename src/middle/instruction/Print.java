package middle.instruction;

public class Print extends INode {
    // @print, printf
    
    private final String string;
    
    public Print(String string) {
        this.string = string;
    }
    
    public String getString() {
        return string;
    }
    
    @Override
    public String toString() {
        return "printf \"" + string + "\"\n";
    }
}
