package middle.instruction;

import middle.optimizer.UseNode;
import middle.val.Address;
import middle.val.Value;

import java.util.ArrayList;

public class Save extends INode implements UseNode {
    private Address dst;//写入到的目标
    private Value src;//要写入的值
    
    public Save(Address dst, Value src) {
        this.dst = dst;
        this.src = src;
    }
    
    public Address getDst() {
        return dst;
    }
    
    public Value getSrc() {
        return src;
    }
    
    @Override
    public ArrayList<Value> getUse() {
        ArrayList<Value> ret = new ArrayList<>();
        ret.add(src);
        ret.add(dst);
        return ret;
    }
    
    @Override
    public void replaceOperands(ArrayList<Value> ops) {
        src = ops.get(0);
        dst = (Address) ops.get(1);
    }
    
    @Override
    public String toString() {
        return dst + " <- " + src + "\n";
    }
}
