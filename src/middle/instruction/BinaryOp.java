package middle.instruction;

import backend.instruction.La;
import middle.LabelTable;
import middle.MiddleState;
import middle.optimizer.UseNode;
import middle.val.Number;
import middle.val.Value;
import middle.val.Variable;

import java.util.ArrayList;

public class BinaryOp extends INode implements DefNode, UseNode {
    //二元操作的动作单元
    //op1, op, op2, out(symbol)
    //输出格式：out = op1 op op2
    public enum Operator {
        ADD("+"),
        SUB("-"),
        MULT("×"),
        DIV("÷"),
        MOD("%"),
        AND("&&"),
        OR("||"),
        EQ("=="),
        NEQ("!="),
        GT(">"),
        GE("≥"),
        LT("<"),
        LE("≤"),
        SLL("<<"),//优化与翻译mips时用
        SRA(">>");//优化与翻译mips时用
        
        private final String str;
        
        Operator(String s) {
            this.str = s;
        }
        
        @Override
        public String toString() {
            return str;
        }
    }
    
    public static int calConst(BinaryOp iCode) {
        Operator op = iCode.op;
        int first = ((Number) iCode.op1).getVal();
        int second = ((Number) iCode.op2).getVal();
        switch (op) {
            case ADD:
                return first + second;
            case SUB:
                return first - second;
            case MULT:
                return first * second;
            case DIV:
                return first / second;
            case MOD:
                return first % second;
            case AND:
                if (first == 0 || second == 0) {
                    return 0;
                } else {
                    return 1;
                }
            case OR:
                if (first == 0 && second == 0) {
                    return 0;
                } else {
                    return 1;
                }
            case EQ:
                if (first != second) {
                    return 0;
                } else {
                    return 1;
                }
            case NEQ:
                if (first == second) {
                    return 0;
                } else {
                    return 1;
                }
            case GE:
                if (first < second) {
                    return 0;
                } else {
                    return 1;
                }
            case GT:
                if (first <= second) {
                    return 0;
                } else {
                    return 1;
                }
            case LE:
                if (first > second) {
                    return 0;
                } else {
                    return 1;
                }
            case LT:
                if (first >= second) {
                    return 0;
                } else {
                    return 1;
                }
            case SLL:
                return first << second;
            case SRA:
                return first >> second;
        }
        System.out.println("BinaryOp: 新增未知运算");
        return 0;
    }
    
    private final Value result;//通常存在一个临时变量里
    private final Operator op;
    private Value op1;
    private Value op2;
    
    public BinaryOp(Value result, Operator op, Value op1, Value op2) {
        this.result = result;
        this.op = op;
        this.op1 = op1;
        this.op2 = op2;
    }
    
    @Override
    public int getSize() {
        if (result.isTemp()) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public Value getDef() {
        return result;
    }
    
    @Override
    public ArrayList<Value> getUse() {
        ArrayList<Value> uses = new ArrayList<>();
        uses.add(op1);
        uses.add(op2);
        return uses;
    }
    
    @Override
    public void replaceOperands(ArrayList<Value> ops) {
        op1 = ops.get(0);
        op2 = ops.get(1);
    }
    
    public Operator getOp() {
        return op;
    }
    
    public Value getOp1() {
        return op1;
    }
    
    public Value getOp2() {
        return op2;
    }
    
    public Value getResult() {
        return result;
    }
    
    public INode optimize() {
        INode newNode = this;
        if (op1 instanceof Number && op2 instanceof Number) {
            int res = calConst(this);
            newNode = new Move(result, new Number(res));
        } else if (op1 instanceof Number || op2 instanceof Number) {
            switch (op) {
                case ADD:
                    if (op1 instanceof Number && ((Number) op1).getVal() == 0) {
                        newNode = new Move(result, op2);
                    } else if (op2 instanceof Number && ((Number) op2).getVal() == 0) {
                        newNode = new Move(result, op1);
                    }
                    break;
                case SUB:
                    if (op1 instanceof Number && ((Number) op1).getVal() == 0) {
                        newNode = new UnaryOp(op2, UnaryOp.Operator.NEG, result);
                    } else if (op2 instanceof Number && ((Number) op2).getVal() == 0) {
                        newNode = new Move(result, op1);
                    }
                    break;
                case MULT:
                    if (op1 instanceof Number) {
                        newNode = optimizeMult((Number) op1, op2);
                    } else {
                        newNode = optimizeMult((Number) op2, op1);
                    }
                    break;
                case DIV:
                    newNode = optimizeDiv();
                    break;
                case MOD:
                    if (op1 instanceof Number && ((Number) op1).getVal() == 0) {
                        newNode = new Move(result, new Number(0));
                    } else if (op2 instanceof Number && (((Number) op2).getVal() == 1 || ((Number) op2).getVal() == -1)) {
                        newNode = new Move(result, new Number(0));
                    }
                    break;
                case AND:
                    if (op1 instanceof Number && ((Number) op1).getVal() == 0 ||
                            op2 instanceof Number && ((Number) op2).getVal() == 0) {
                        newNode = new Move(result, new Number(0));
                    } else if (op1 instanceof Number && ((Number) op1).getVal() == 1) {
                        newNode = new Move(result, op2);
                    } else if (op2 instanceof Number && ((Number) op2).getVal() == 1) {
                        newNode = new Move(result, op1);
                    }
                    break;
                case OR:
                    if (op1 instanceof Number && ((Number) op1).getVal() != 0 ||
                            op2 instanceof Number && ((Number) op2).getVal() != 0) {
                        newNode = new Move(result, new Number(1));
                    } else if (op1 instanceof Number && ((Number) op1).getVal() == 0) {
                        newNode = new Move(result, op2);
                    } else if (op2 instanceof Number && ((Number) op2).getVal() == 0) {
                        newNode = new Move(result, op1);
                    }
                    break;
                default:
                    break;
            }
        }
        
        return newNode;
    }
    
    private INode optimizeMult(Number op1, Value op2) {
        INode newNode = this;
        int num1 = op1.getVal();
        if (num1 == 0) {
            newNode = new Move(result, new Number(0));
        } else if (num1 == 1) {
            newNode = new Move(result, op2);
        } else if (num1 == -1) {
            newNode = new UnaryOp(op2, UnaryOp.Operator.NEG, result);
        } else if (Integer.bitCount(num1) == 1) {
            //TODO 2位的也可以考虑优化掉
            int pos = 0;
            while ((num1 & (1 << pos)) == 0) {
                pos++;
            }
            newNode = new BinaryOp(result, Operator.SLL, op2, new Number(pos));
        } else if (Integer.bitCount(-num1) == 1) {
            // old: y = -8*x
            // new: y = x<<3
            //      y = -y
            //TODO 2位的也可以考虑优化掉
            num1 = -num1;
            int pos = 0;
            while ((num1 & (1 << pos)) == 0) {
                pos++;
            }
            Variable tmp = new Variable(String.valueOf(MiddleState.tmpCnt++));
            newNode = new BinaryOp(tmp, Operator.SLL, op2, new Number(pos));//TODO 考虑不使用ssa原地替换?
            newNode.insert(new UnaryOp(tmp, UnaryOp.Operator.NEG, result));//res不能改变
        } else if (Integer.bitCount(num1) == 2) {
            // 2位的也可以考虑优化掉
            int pos = 0;
            while ((num1 & (1 << pos)) == 0) {
                pos++;
            }
            Variable tmp1 = new Variable(String.valueOf(MiddleState.tmpCnt++));
            Variable tmp2 = new Variable(String.valueOf(MiddleState.tmpCnt++));
            newNode = new BinaryOp(tmp1, Operator.SLL, op2, new Number(pos));
            pos++;
            while ((num1 & (1 << pos)) == 0) {
                pos++;
            }
            INode next = new BinaryOp(tmp2, Operator.SLL, op2, new Number(pos));
            next.insert(new BinaryOp(result, Operator.ADD, tmp1, tmp2));
            newNode.insert(next);
        } else if (Integer.bitCount(-num1) == 2) {
            // 2位的也可以考虑优化掉
            num1 = -num1;
            int pos = 0;
            while ((num1 & (1 << pos)) == 0) {
                pos++;
            }
            Variable tmp1 = new Variable(String.valueOf(MiddleState.tmpCnt++));
            Variable tmp2 = new Variable(String.valueOf(MiddleState.tmpCnt++));
            Variable tmp3 = new Variable(String.valueOf(MiddleState.tmpCnt++));
            newNode = new BinaryOp(tmp1, Operator.SLL, op2, new Number(pos));
            pos++;
            while ((num1 & (1 << pos)) == 0) {
                pos++;
            }
            INode next = new BinaryOp(tmp2, Operator.SLL, op2, new Number(pos));
            newNode.insert(next);
            next = next.insert(new BinaryOp(tmp3, Operator.ADD, tmp1, tmp2));
            next.insert(new UnaryOp(tmp3, UnaryOp.Operator.NEG, result));//TODO 考虑不使用ssa原地替换?
        }
        if (newNode != this) {
            System.out.println("BinaryOp: opt mult");
            System.out.println(newNode);
            System.out.println(this);
        }
        return newNode;
    }
    
    private INode optimizeDiv() {
        INode newNode = this;
        if (op1 instanceof Number && (((Number) op1).getVal() == 0)) {
            newNode = new Move(result, new Number(0));
        } else if (op2 instanceof Number) {
            int num2 = ((Number) op2).getVal();
            if (num2 == 1) {
                newNode = new Move(result, op1);
            } else if (num2 == -1) {
                newNode = new UnaryOp(op1, UnaryOp.Operator.NEG, result);
            }
            // 负数的右移会出问题
        }
        if (newNode != this) {
            System.out.println("BinaryOp: opt div");
            System.out.println(newNode);
            System.out.println(this);
        }
        return newNode;
    }
    
    @Override
    public String toString() {
        return result + " = " + op1 + " " + op + " " + op2 + "\n";
    }
}
