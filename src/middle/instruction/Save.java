package middle.instruction;

import middle.val.Address;
import middle.val.Value;

public class Save extends INode {
    private final Address dst;//写入到的目标
    private final Value src;//要写入的值
    
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
    public String toString() {
        return dst + " <- " + src + "\n";
    }
}
