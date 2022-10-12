package middle.instruction;

import middle.val.Address;
import middle.val.Value;

public class Save extends INode {
    private final Address dst;//加载到的目标
    private final Value src;//要加载的地址
    
    public Save(Address dst, Value src) {
        this.dst = dst;
        this.src = src;
    }
    
    @Override
    public String toString() {
        return dst + " <- " + src + "\n";
    }
}
