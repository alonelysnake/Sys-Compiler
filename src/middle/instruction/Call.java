package middle.instruction;

public class Call extends INode {
    //动作序列：@Call, func name👇 + paras👇 + ret👆
    private final String label;//调用的对应函数的入口的label
    
    public Call(String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return "call function" + label + "\n";
    }
}
