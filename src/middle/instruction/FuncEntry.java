package middle.instruction;

public class FuncEntry extends INode {
    //函数入口，在函数定义时生成
    private final String label;//函数入口的标签
    private final int paraNum;//函数参数个数
    
    public FuncEntry(String label, int paraNum) {
        this.label = label;
        this.paraNum = paraNum;
    }
    
    @Override
    public String toString() {
        return label + ": param num = " + paraNum + "\n";
    }
}
