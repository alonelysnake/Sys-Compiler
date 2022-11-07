package middle.instruction;

import middle.LabelTable;

public class Jump extends INode {
    // 函数跳转jal，或强制跳转j（如何判断用j还是jal?）
    
    private String label;//要跳转到的标签
    
    public Jump(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public INode optimize(LabelTable labelTable) {
        INode labelNode = labelTable.getNode(this.label);
        INode node = getNext();
        while (node != this && node != null) {
            if (!(node instanceof Nop)) {
                break;
            }
            node = node.getNext();
        }
        if (node == labelNode) {
            return new Nop();
        }
        return this;
    }
    
    @Override
    public String toString() {
        return "jump " + label + "\n";
    }
}
