package middle.instruction;

import middle.optimizer.UseNode;
import middle.val.Address;
import middle.val.Value;

import java.util.ArrayList;
import java.util.Collection;

public class Load extends INode implements DefNode, UseNode {
    private final Value dst;//加载到的目标
    private Address addr;//要加载的地址
    
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
    public ArrayList<Value> getUse() {
        ArrayList<Value> ret = new ArrayList<>();
        ret.add(addr);
        return ret;
    }
    
    @Override
    public void replaceOperands(ArrayList<Value> ops) {
        addr = (Address) ops.get(0);
    }
    
    @Override
    public Value getDef() {
        return dst;
    }
    
    @Override
    public String toString() {
        return dst + " <- " + addr + "\n";
    }
}
