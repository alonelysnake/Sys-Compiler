package backend.schedule;

import backend.element.Reg;
import backend.optimizer.Allocator;
import middle.instruction.INode;
import middle.val.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class OptimizeScheduler implements Scheduler {
    //使用着色图优化后的寄存器分配策略
    private final HashMap<String, Allocator> funcAllocators;//函数名func_xxx-函数的分配器
    private final LinkedList<Reg> tempFrees = new LinkedList<>(Arrays.asList(
            Reg.T0, Reg.T1, Reg.T2/*, Reg.T3, Reg.T4,
            Reg.T5, Reg.T6, Reg.T7, Reg.T8, Reg.T9*/));
    private final LinkedList<Reg> tempUses = new LinkedList<>();
    private final HashMap<Reg, Value> cur = new HashMap<>();
    private String curFunc;
    private final HashMap<Value, Reg> globalLoad = new HashMap<>();//记录变量是否已经被加载到全局寄存器，针对如下情况：
    /*
    a=1;    a<->$s0
    if(xxx){
        b=1;    b<->$s0
        ...
        if 中没用到a, if 后也没用到a，故a不在此处的out集，不和b冲突
    }else{
        a=2;    a<->$s0
        ...
    }
    此后a不再出现
    a b共用一个寄存器，故在翻译else时cur中没有a的绑定关系
     */
    
    public OptimizeScheduler(HashMap<String, Allocator> funcAllocators) {
        this.funcAllocators = funcAllocators;
    }
    
    @Override
    public Reg alloc(Value val) {
        Reg ret = funcAllocators.get(curFunc).getGlobalReg(val);
        if (ret != null) {
            globalLoad.put(val, ret);
        } else if (!tempFrees.isEmpty()) {
            //不能分配全局寄存器
            ret = tempFrees.removeFirst();
            tempUses.addLast(ret);
        }
        if (ret != null) {
            cur.put(ret, val);//此处包含了寄存器的添加或是对全局寄存器的替换
        }
        return ret;
    }
    
    @Override
    public void free(Reg reg) {
        cur.remove(reg);
        //有可能是全局寄存器?
        if (tempUses.contains(reg)) {
            tempFrees.addLast(reg);
            tempUses.remove(reg);
        }
    }
    
    @Override
    public Reg possibleFree(INode node, ArrayList<Reg> forbids) {
        Reg ret = null;
        for (Reg reg : tempUses) {
            if (!forbids.contains(reg) && !isActive(node, cur.get(reg))) {
                ret = reg;
                break;
            }
        }
        if (ret == null) {
            for (Reg reg : tempUses) {
                if (!forbids.contains(reg)) {
                    ret = reg;
                    break;
                }
            }
        }
        return ret;
    }
    
    @Override
    public Value replace(Reg reg, Value value) {
        // 此处为对临时寄存器的替换，全局寄存器的替换在alloc阶段即可实现
        Value oldVal = cur.get(reg);
        cur.replace(reg, value);
        if (tempUses.contains(reg)) {
            //应该恒为true
            tempUses.remove(reg);
            tempUses.addLast(reg);
        } else {
            System.err.println("optimizeScheduler.replace: 未分配的或非临时寄存器被替换");
        }
        return oldVal;
    }
    
    @Override
    public void clear() {
        cur.clear();
        globalLoad.clear();
        while (!tempUses.isEmpty()) {
            tempFrees.add(tempUses.removeFirst());
        }
    }
    
    @Override
    public Reg val2reg(Value val) {
        for (Reg reg : cur.keySet()) {
            if (val.equals(cur.get(reg))) {
                //TODO 对比临时寄存器采用lru和FIFO?
                if (tempUses.contains(reg)) {
                    tempUses.remove(reg);
                    tempUses.addLast(reg);
                }
                return reg;
            }
        }
        //存在val对应的全局寄存器已加载但cur中被其他共用一个全局寄存器的占用的情况
        if (globalLoad.containsKey(val)) {
            cur.put(globalLoad.get(val), val);
            return globalLoad.get(val);
        }
        return null;
    }
    
    @Override
    public Value reg2val(Reg reg) {
        return cur.get(reg);
    }
    
    @Override
    public HashMap<Reg, Value> getCurrentContext(INode node) {
        //TODO 更新寄存器的绑定状态，把所有globalLoad中的寄存器恢复到cur里，防止函数跳转时cur中不是正确上下文，错误判断全局寄存器的active状态
        for (Value value : globalLoad.keySet()) {
            if (isActive(node, value)) {
                cur.put(globalLoad.get(value), value);
            }
        }
        return cur;
    }
    
    @Override
    public void funcCall(String newFunc) {
        this.curFunc = newFunc;
        clear();
    }
    
    @Override
    public boolean isActive(INode curINode, Value val) {
        //TODO 活跃相关的判断条件（活跃判断有误还是非活跃(全局)变量需要回存?）
        return funcAllocators.get(curFunc).isActive(curINode, val);
    }
    
    @Override
    public boolean isGlobal(Reg reg) {
        return Reg.GLOBAL_HEAP.contains(reg);
    }
}
