package middle.instruction;

public class Jump extends INode {
    // 函数跳转jal，或强制跳转j（如何判断用j还是jal?）
    
    private final String label;//要跳转到的标签
    
    public Jump(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    @Override
    public String toString() {
        return "jump " + label + "\n";
    }
}
