package middle.instruction;

import middle.val.Value;

public class Return extends INode {
    // jr，此外可能会有move v0
    public static final String RET_REG = "$v0";// 在函数调用后固定的寄存器v0
    
    private final Value ret;//可能有的返回值
    
    // 无返回值
    public Return() {
        this.ret = null;
    }
    
    //有返回值
    public Return(Value ret) {
        this.ret = ret;
    }
    
    public Value getRet() {
        return ret;
    }
    
    @Override
    public String toString() {
        if (ret != null) {
            return "return " + ret + "\n";
        }
        return "return\n";
    }
}
