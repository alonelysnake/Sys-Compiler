package middle.instruction;

import middle.optimizer.UseNode;
import middle.val.Address;
import middle.val.Value;
import middle.val.Variable;

import java.util.ArrayList;

public class FetchParam extends INode implements DefNode, UseNode {
    private Value para;//形参名
    
    public FetchParam(Value para) {
        this.para = para;
    }
    
    @Override
    public int getSize() {
        return 1;
    }
    
    public Value getPara() {
        return para;
    }
    
    @Override
    public Value getDef() {
        return para;
    }
    
    @Override
    public ArrayList<Value> getUse() {
        ArrayList<Value> ret = new ArrayList<>();
        ret.add(para);
        return ret;
    }
    
    @Override
    public void replaceOperands(ArrayList<Value> ops) {
        //应该不可达?
        if (this.para != ops.get(0)) {
            System.err.println("FetchParam.replaceOperands：形参不应被优化为其他值");
        }
        this.para = ops.get(0);
    }
    
    @Override
    public String toString() {
        if (para instanceof Address) {
            return "param addr " + para + "\n";
        } else if (para instanceof Variable) {
            return "param var" + para + "\n";
        } else {
            System.err.println("中间代码形参类型错误");
            return "";
        }
    }
}
