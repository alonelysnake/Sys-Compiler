package backend.schedule;

import backend.element.Reg;
import middle.instruction.INode;
import middle.val.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class BasicScheduler implements Scheduler {
    private final HashMap<Reg, Value> cur = new HashMap<>();
    private final LinkedList<Reg> frees = new LinkedList<>(Arrays.asList(
            Reg.T0, Reg.T1, Reg.T2, Reg.T3, Reg.T4,
            Reg.T5, Reg.T6, Reg.T7, Reg.T8, Reg.T9,
            Reg.S12, Reg.S13, Reg.S0, Reg.S1, Reg.S2,
            Reg.S3, Reg.S4, Reg.S5, Reg.S6, Reg.S7,
            Reg.S8, Reg.S9, Reg.S10, Reg.S11, Reg.S14));
    private final LinkedList<Reg> uses = new LinkedList<>();
    
    @Override
    public Reg alloc(Value val) {
        if (!frees.isEmpty()) {
            Reg reg = frees.removeFirst();
            uses.addLast(reg);
            cur.put(reg, val);
            return reg;
        }
        return null;
    }
    
    @Override
    public void free(Reg reg) {
        cur.remove(reg);
        frees.addLast(reg);
        uses.remove(reg);
    }
    
    @Override
    public Reg possibleFree(ArrayList<Reg> forbids) {
        for (Reg reg : uses) {
            if (!forbids.contains(reg)) {
                return reg;
            }
        }
        return null;
    }
    
    @Override
    public Value replace(Reg reg, Value value) {
        cur.replace(reg, value);
        uses.remove(reg);
        uses.addLast(reg);
        return null;
    }
    
    @Override
    public void clear() {
        cur.clear();
        while (!uses.isEmpty()) {
            Reg reg = uses.removeFirst();
            frees.add(reg);
        }
    }
    
    @Override
    public Reg val2reg(Value val) {
        for (Reg reg : cur.keySet()) {
            if (val.equals(cur.get(reg))) {
                return reg;
            }
        }
        return null;
    }
    
    @Override
    public Value reg2val(Reg reg) {
        return cur.get(reg);
    }
    
    @Override
    public HashMap<Reg, Value> getCurrentContext() {
        return cur;
    }
    
    @Override
    public boolean isActive(INode curINode, Value val) {
        return true;
    }
    
    @Override
    public boolean isGlobal(Reg reg) {
        // 所有寄存器均当作临时寄存器处理
        return false;
    }
}
