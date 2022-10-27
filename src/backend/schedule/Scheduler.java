package backend.schedule;

import backend.element.Reg;
import middle.instruction.INode;
import middle.val.Value;

import java.util.ArrayList;
import java.util.HashMap;

public interface Scheduler {
    /**
     * 输入待分配寄存器的变量，返回分配给该变量的寄存器
     * @param val
     * @return
     */
    Reg alloc(Value val);
    
    /**
     * 输入要释放的寄存器
     * @param reg
     */
    void free(Reg reg);
    
    /**
     * 输入禁止覆盖的寄存器组，输出可以被覆盖的寄存器
     * @param forbids
     * @return
     */
    Reg possibleFree(ArrayList<Reg> forbids);
    
    /**
     * 将Reg替换为新的值，并返回被替换的变量
     * @param reg
     * @param value
     * @return
     */
    Value replace(Reg reg,Value value);
    
    /**
     * 清空寄存器
     */
    void clear();
    
    /**
     * 得到变量绑定的寄存器
     * @param val
     * @return
     */
    Reg val2reg(Value val);
    
    /**
     * 寄存器内的变量
     * @param reg
     * @return
     */
    Value reg2val(Reg reg);
    
    /**
     * 得到目前的寄存器绑定状态
     * @return
     */
    HashMap<Reg,Value>getCurrentContext();
    
    /**
     * 判断val变量在此中间代码处是否还活跃
     * @param curINode
     * @param val
     * @return
     */
    boolean isActive(INode curINode, Value val);
}
