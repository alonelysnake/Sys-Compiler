package middle.instruction;

import middle.val.Address;
import middle.val.Value;

public class Load extends INode {
    private final Value dst;//加载到的目标
    private final Address addr;//要加载的地址
    
    public Load(Value dst, Address addr) {
        this.dst = dst;
        this.addr = addr;
    }
    
    @Override
    public String toString() {
        return dst + " <- " + addr + "\n";
    }
}
