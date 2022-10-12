package middle;

import middle.instruction.INode;
import middle.val.Value;

public class BlockInfo {
    //此模块不仅代表block，而是指提取出的一串返回的值，即综合属性
    private final Value ret;//模块的返回值
    private final INode first;//第一个元素
    private final INode last;//最后一个元素
    
    public BlockInfo(Value ret, INode first, INode last) {
        this.ret = ret;
        this.first = first;
        this.last = last;
    }
    
//    public BlockInfo(Value ret, INode block) {
//        this.ret = ret;
//        INode p = block;
//        while (p.getPrev() != null) {
//            p = p.getPrev();
//        }
//        this.first = p;
//        p = block;
//        while (p.getNext() != null) {
//            p = p.getNext();
//        }
//        this.last = p;
//    }
    
    public INode getFirst() {
        return first;
    }
    
    public INode getLast() {
        return last;
    }
    
    public Value getRetVal() {
        return ret;
    }
}
