package middle.instruction;

import middle.LabelTable;
import middle.optimizer.UseNode;
import middle.val.Number;
import middle.val.Value;

import java.util.ArrayList;

public class Branch extends INode implements UseNode {
    //分支语句
    //在中间代码中规定right恒为0
    // if left <op> right, then jump to label
    public enum Operator {
        EQ("=="),
        NEQ("!="),
        GT(">"),
        GE("≥"),
        LT("<"),
        LE("≤");
        
        private final String str;
        
        Operator(String s) {
            this.str = s;
        }
        
        @Override
        public String toString() {
            return str;
        }
    }
    
    private Value left;//分支语句判断的左操作数
    private Value right;//右操作数，实际固定为0或1
    private final Operator op;
    private String label;//满足情况时的跳转标签
    
    public Branch(Value left, Value right, Operator op, String label) {
        this.left = left;
        this.right = right;
        this.op = op;
        this.label = label;
    }
    
    public Value getLeft() {
        return left;
    }
    
    public Value getRight() {
        return right;
    }
    
    public Operator getOp() {
        return op;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
    public ArrayList<Value> getUse() {
        ArrayList<Value> uses = new ArrayList<>();
        uses.add(left);
        uses.add(right);
        return uses;
    }
    
    @Override
    public void replaceOperands(ArrayList<Value> ops) {
        left = ops.get(0);
        right = ops.get(1);
    }
    
    public boolean calConst(Operator op, int left, int right) {
        switch (op) {
            case EQ:
                return left == right;
            case NEQ:
                return left != right;
            case GE:
                return left >= right;
            case GT:
                return left > right;
            case LE:
                return left <= right;
            case LT:
                return left < right;
            default:
                return false;
        }
    }
    
    public INode optimize(LabelTable labelTable) {
        INode labelNode = labelTable.getNode(this.label);
        INode node = getNext();
        while (node != this && node != null) {
            if (!(node instanceof Nop)) {
                break;
            }
            node = node.getNext();
        }
        if (labelNode == node) {
            return new Nop();
        } else if (left instanceof Number && right instanceof Number) {
            if (calConst(op, ((Number) left).getVal(), ((Number) right).getVal())) {
                return new Jump(label);
            } else {
                return new Nop();
            }
        } else {
            // TODO 不会出现left是立即数，right是变量的情况
            return this;
        }
    }
    
    @Override
    public String toString() {
        return "if " + left + " " + op + " " + right + ", branch " + label + "\n";
    }
}
