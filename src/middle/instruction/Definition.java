package middle.instruction;

import middle.val.Value;

import java.util.ArrayList;

public class Definition extends INode implements StackSpace {
    private final boolean global;// 是否为全局变量
    private final boolean constFlag;// 是否为常量
    private final Value name;// 变量名（和原来的名字会有些区别）
    private final int size;// 容量（二维数组展平成一维数组，寻址操作在中间代码处理时已计算出偏移量）
    private final ArrayList<Value> initVals;// 初始值（不一定和容量相同）
    
    public Definition(boolean global, boolean constFlag, Value name, int size, ArrayList<Value> initVals) {
        this.global = global;
        this.constFlag = constFlag;
        this.name = name;
        this.size = size;
        this.initVals = initVals;
    }
    
    public Value getName() {
        return name;
    }
    
    @Override
    public int getSize() {
        return size;
    }
    
    @Override
    public Value getNewVar() {
        return name;
    }
    
    public ArrayList<Value> getInitVals() {
        return initVals;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (global) {
            sb.append("global ");
        } else {
            sb.append("local ");
        }
        if (constFlag) {
            sb.append("const ");
        } else {
            sb.append("var ");
        }
        sb.append(name);
        sb.append(", size = ");
        sb.append(size);
        sb.append(", value = ");
        for (Value value : initVals) {
            sb.append(value);
            sb.append(" ");
        }
        sb.append("\n");
        return sb.toString();
    }
}
