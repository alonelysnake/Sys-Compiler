package middle.optimizer;

import middle.instruction.INode;
import utils.LinkNode;

import java.util.Objects;

public class BasicBlock extends LinkNode<BasicBlock> {
    //基本块
    public static int cnt;
    private INode first;
    private INode last;
    private int index;
    
    public BasicBlock(INode first, INode last) {
        this.first = first;
        this.last = last;
        this.index = cnt++;
    }
    
    public INode getFirst() {
        return first;
    }
    
    public void setFirst(INode first) {
        this.first = first;
    }
    
    public INode getLast() {
        return last;
    }
    
    public void setLast(INode last) {
        this.last = last;
    }
    
    //TODO equals方法?
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicBlock block = (BasicBlock) o;
        return index == block.index && Objects.equals(first, block.first) && Objects.equals(last, block.last);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
    
    public String getName() {
        return "Block" + index;
    }
    
    @Override
    public String toString() {
        //debug用
        StringBuilder sb = new StringBuilder();
        INode node = first;
        while (node != null) {
            sb.append(node).append("\n");
            if (node == last) {
                break;
            }
            node = node.getNext();
        }
        return sb.toString();
    }
}
