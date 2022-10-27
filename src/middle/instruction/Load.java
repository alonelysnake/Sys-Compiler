package middle.instruction;

import middle.val.Address;
import middle.val.Value;

public class Load extends INode implements StackSpace {
    private final Value dst;//加载到的目标
    private final Address addr;//要加载的地址
    
    public Load(Value dst, Address addr) {
        this.dst = dst;
        this.addr = addr;
    }
    
    public Value getDst() {
        return dst;
    }
    
    public Address getAddr() {
        return addr;
    }
    
    @Override
    public int getSize() {
        if (dst.isTemp()) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public Value getNewVar() {
        return dst;
    }
    
    @Override
    public String toString() {
        return dst + " <- " + addr + "\n";
    }
}
