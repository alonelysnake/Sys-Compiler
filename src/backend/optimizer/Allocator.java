package backend.optimizer;

import backend.element.Reg;
import middle.BlockInfo;
import middle.LabelTable;
import middle.instruction.DefNode;
import middle.instruction.INode;
import middle.optimizer.BasicBlock;
import middle.optimizer.Optimizer;
import middle.optimizer.UseNode;
import middle.val.Number;
import middle.val.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Allocator extends Optimizer {
    private final ConflictMap conflictMap = new ConflictMap();
    private final HashMap<Value, Reg> val2reg = new HashMap<>();
    
    public Allocator(BlockInfo block, LabelTable labelTable) {
        super(block, labelTable);
    }
    
    public BlockInfo optimize() {
        analyze();
        createConflictMap();
        draw();
        
        return null;
    }
    
    protected void calDefUse() {
        BasicBlock block = firstBlock;
        while (block != null) {
            HashSet<Value> newDefs = new HashSet<>();
            HashSet<Value> newUses = new HashSet<>();
            
            INode code = block.getLast();
            final INode firstNode = block.getFirst();
            while (code != null) {
                if (code instanceof DefNode) {
                    Value def = ((DefNode) code).getDef();
                    newDefs.add(def);
                    if (!varDefNode.containsKey(def)) {
                        varDefNode.put(def, new HashSet<>());
                    }
                    varDefNode.get(def).add(code);
                    newUses.remove(def);
                }
                if (code instanceof UseNode) {
                    ArrayList<Value> use = ((UseNode) code).getUse();
                    use.forEach(value -> {
                        if (!(value instanceof Number)) newUses.add(value);
                    });
                }
                if (code == firstNode) {
                    break;
                }
                code = code.getPrev();
            }
            
            defs.put(block, newDefs);
            uses.put(block, newUses);
            activeVarIn.put(block, new HashSet<>(newUses));// 同时初始化in out集
            activeVarOut.put(block, new HashSet<>());
            
            block = block.getNext();
        }
    }
    
    private void createConflictMap() {
        BasicBlock block = firstBlock;
        while (block != null) {
            INode node = block.getLast();
            final INode firstNode = block.getFirst();
            HashSet<Value> blockOut = new HashSet<>(activeVarOut.get(block));
            while (node != null) {
                if (node instanceof DefNode) {
                    Value def = ((DefNode) node).getDef();
                    if (!def.isGlobal()) {
                        conflictMap.addNode(def);
                        if (node instanceof UseNode) {
                            blockOut.forEach(v -> {
                                if (!(v instanceof Number) && !v.isGlobal()) conflictMap.link(v, def);//TODO 应该是use和out?
                            });
//                            for (Value use : ((UseNode) node).getUse()) {
//                                blockOut.forEach(v -> {
//                                    if (!(v instanceof Number) && !v.isGlobal())
//                                        conflictMap.link(v, use);//TODO 应该是use和out还是def out?
//                                });
//                            }
                        }
                    }
                    blockOut.remove(def);
                }
                if (node instanceof UseNode) {
                    ((UseNode) node).getUse().forEach(v -> {
                        if (!(v instanceof Number) && !v.isGlobal()) blockOut.add(v);//不考虑全局量
                    });
                }
                if (node == firstNode) {
                    break;
                }
                node = node.getPrev();
            }
            block = block.getNext();
        }
    }
    
    private void draw() {
        int regNum = Reg.GLOBAL_HEAP.size();
        Stack<Value> nodes = new Stack<>();//栈式记录冲突图中的所有value
        Stack<HashSet<Value>> edges = new Stack<>();//移除value时value对应的边
        HashSet<Value> noColors = new HashSet<>();//一定无法分配颜色的节点
        // 依次遍历冲突图内的所有节点，根据此时的度数选择
        while (!conflictMap.isEmpty()) {
            Value value = conflictMap.getColorable(regNum);
            if (value == null) {
                //TODO 选择一个最适合被移除的value
                value = getRemoveVal();
                noColors.add(value);
            }
            nodes.push(value);
            edges.push(conflictMap.getConnectNodes(value));
            //无论是否可以着色，都从冲突图中移除
            conflictMap.remove(value);
        }
        while (!nodes.isEmpty()) {
            Value node = nodes.pop();
            HashSet<Value> edge = edges.pop();
            //TODO 是否要回填恢复冲突图?
            ArrayList<Reg> freeRegs = new ArrayList<>(Reg.GLOBAL_HEAP);
            edge.forEach(v -> freeRegs.remove(val2reg.get(v)));
//            ArrayList<Reg> freeRegs = new ArrayList<>(regHeap);
//            for(Value value:edge){
//                if(val2reg.containsKey(value)){
//                    freeRegs.remove(val2reg.get(value));
//                }
//            }
            if (!freeRegs.isEmpty()) {
                val2reg.put(node, freeRegs.get(0));
            }
        }
    }
    
    private Value getRemoveVal() {
        // 得到最适合不分配全局寄存器的变量
        Set<Value> curVals = conflictMap.getAllNodes();
        int maxGrade = 0;
        Value ret = null;
        for (Value value : curVals) {
            int grade = 1;//TODO 计算分数的标准?
            if (grade > maxGrade) {
                maxGrade = grade;
                ret = value;
            }
        }
        return ret;
    }
    
    public Reg getGlobalReg(Value value) {
        return val2reg.get(value);
    }
    
    /**
     * 在node处，val是否活跃
     *
     * @param curNode
     * @param val
     * @return
     */
    public boolean isActive(INode curNode, Value val) {
        //先加入out集，再从后向前遍历所在基本块，先删除def，再加入usage
        BasicBlock curBlock = flowGraph.getBlock(curNode);
        HashSet<Value> out = new HashSet<>(activeVarOut.get(curBlock));
        INode node = curBlock.getLast();
        while (node != curNode) {
            if (node instanceof DefNode) {
                out.remove(((DefNode) node).getDef());
            }
            if (node instanceof UseNode) {
                ((UseNode) node).getUse().forEach(v -> {
                    if (!(v instanceof Number)) out.add(v);
                });
            }
            //TODO 是否还需要 block.getFirst()的限制保证?
            node = node.getPrev();
        }
        return out.contains(val);
    }
}
