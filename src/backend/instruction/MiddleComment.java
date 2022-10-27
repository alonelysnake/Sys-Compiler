package backend.instruction;

import middle.instruction.INode;

public class MiddleComment extends MIPSCode {
    //调试用，展示对应的中间代码
    private final String str;
    
    public MiddleComment(INode iNode) {
        str = "# " + iNode.toString();
    }
    
    @Override
    public String toString() {
        return str;
    }
}
