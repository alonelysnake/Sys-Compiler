package middle.optimizer;

import middle.BlockInfo;
import middle.LabelTable;
import middle.instruction.BinaryOp;
import middle.instruction.Branch;
import middle.instruction.DefNode;
import middle.instruction.Definition;
import middle.instruction.FetchParam;
import middle.instruction.INode;
import middle.instruction.Input;
import middle.instruction.Jump;
import middle.instruction.Move;
import middle.instruction.Nop;
import middle.instruction.Return;
import middle.instruction.UnaryOp;
import middle.val.Number;
import middle.val.Value;
import middle.val.Variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Optimizer {
    private boolean changed;
    private INode firstINode;
    private INode lastINode;
    private LabelTable labelTable;
    // 基本块链表
    protected BasicBlock firstBlock;
    protected BasicBlock lastBlock;
    
    protected final FlowGraph flowGraph = new FlowGraph();
    //活跃变量分析用，计算每个基本块内的活跃变量
    protected final HashMap<BasicBlock, HashSet<Value>> defs = new HashMap<>();
    protected final HashMap<BasicBlock, HashSet<Value>> uses = new HashMap<>();
    protected final HashMap<BasicBlock, HashSet<Value>> activeVarIn = new HashMap<>();
    protected final HashMap<BasicBlock, HashSet<Value>> activeVarOut = new HashMap<>();
    
    protected final HashMap<Value, HashSet<INode>> varDefNode = new HashMap<>();//在活跃变量分析中生成，在到达定义分析中使用，表示定义某变量的所有语句
    //到达定义分析用，计算每个基本块内的中间代码
    private final HashMap<BasicBlock, HashSet<INode>> gens = new HashMap<>();
    private final HashMap<BasicBlock, HashSet<INode>> kills = new HashMap<>();
    private final HashMap<BasicBlock, HashSet<INode>> reachDefIn = new HashMap<>();
    private final HashMap<BasicBlock, HashSet<INode>> reachDefOut = new HashMap<>();
    
    public Optimizer(BlockInfo block, LabelTable labelTable) {
        this.firstINode = block.getFirst();
        this.lastINode = block.getLast();
        this.labelTable = labelTable;
    }
    
    public BlockInfo optimize() {
        //TODO 优化 first 到 last 段的中间代码，返回优化后的代码
        // 初步优化（不涉及基本块的构造）
        removeNopOptimize();
        branchJumpOptimize();
        constOptimize();
        
        analyze();
        // 变量传播优化
        spreadOptimize();
        return new BlockInfo(null, firstBlock.getFirst(), lastBlock.getLast());
    }
    
    protected void analyze() {
        // 创建流图 flowGraph，设置入口与出口 inBlock outBlock
        createFLowGraph();
        // 活跃变量分析
        calDefUse();
        calActiveVarInOut();
        // 到达定义分析
        calGenKill();
        calReachDefInOut();
    }
    
    /**
     * 判断优化是否完成
     *
     * @return: true if may continue to optimize
     */
    public boolean isChanged() {
        return changed;
    }
    
    //流图创建
    private void createFLowGraph() {
        // 分割基本块
        INode nop = new Nop();
        firstBlock = new BasicBlock(nop, nop);
        lastBlock = firstBlock;
        INode node = firstINode;
        INode blockFirestNode = node;
        HashMap<String, BasicBlock> label2Block = new HashMap<>();
        //TODO 最后一个块如何处理
        while (node != null) {
            ArrayList<String> nextLabels = null;
            if (node.getNext() != null) {
                nextLabels = labelTable.getLabels(node.getNext());
            }
            if ((nextLabels != null && !nextLabels.isEmpty()) || node instanceof Branch || node instanceof Jump || node instanceof Return) {
                BasicBlock block = new BasicBlock(blockFirestNode, node);
                ArrayList<String> curBlockLabels = labelTable.getLabels(blockFirestNode);
                if (curBlockLabels != null) {
                    curBlockLabels.forEach(label -> label2Block.put(label, block));
                }
                lastBlock = lastBlock.insert(block);
                blockFirestNode = node.getNext();
            }
            // 切分的函数块不一定是null
            if (node == lastINode) {
                break;
            }
            node = node.getNext();
        }
        BasicBlock first = firstBlock.getNext();
        firstBlock.remove();
        firstBlock = first;
        
        // 建立流图
        BasicBlock block = firstBlock;
        while (block != null) {
            INode blockNode = block.getFirst();
            while (true) {
                flowGraph.connectINodeBlock(blockNode, block);
                if (blockNode == block.getLast()) {
                    break;
                }
                blockNode = blockNode.getNext();
            }
            block = block.getNext();
        }
        block = firstBlock;
        //TODO 最后一个块是后继的判断?
        while (block != null) {
            if (block.getLast() instanceof Jump) {
                //jump
                flowGraph.link(block, label2Block.get(((Jump) block.getLast()).getLabel()));
            } else if (block.getLast() instanceof Branch) {
                //branch
                flowGraph.link(block, label2Block.get(((Branch) block.getLast()).getLabel()));
                if (block.getNext() != null) {
                    flowGraph.link(block, block.getNext());
                }
            } else if (!(block.getLast() instanceof Return)) {
                //顺序
                if (block.getNext() != null) {
                    flowGraph.link(block, block.getNext());
                }
            }
            block = block.getNext();
        }
    }
    
    //活跃变量 def use 初始化
    protected void calDefUse() {
        BasicBlock block = firstBlock;
        while (block != null) {
            HashSet<Value> newDefs = new HashSet<>();
            HashSet<Value> newUses = new HashSet<>();
            
            INode code = block.getFirst();
            final INode lastCode = block.getLast();
            while (code != null) {
                if (code instanceof DefNode) {
                    Value def = ((DefNode) code).getDef();
                    newDefs.add(def);
                    if (!varDefNode.containsKey(def)) {
                        varDefNode.put(def, new HashSet<>());
                    }
                    varDefNode.get(def).add(code);
                }
                if (code instanceof UseNode) {
                    ArrayList<Value> use = ((UseNode) code).getUse();
                    use.forEach(value -> {
                        if (!(value instanceof Number)) newUses.add(value);
                    });
                }
                if (code == lastCode) {
                    break;
                }
                code = code.getNext();
            }
            
            defs.put(block, newDefs);
            uses.put(block, newUses);
            activeVarIn.put(block, new HashSet<>(newUses));// 同时初始化in out集
            activeVarOut.put(block, new HashSet<>());
            
            block = block.getNext();
        }
    }
    
    // 活跃变量的 in out 分析
    private void calActiveVarInOut() {
        boolean changeFlag = true;// 标识out集是否改变
        while (changeFlag) {
            changeFlag = false;
            BasicBlock block = lastBlock;//从后向前遍历（基于后继）
            while (block != null) {
                HashSet<Value> out = activeVarOut.get(block);
                int beforeOutSize = out.size();
                for (BasicBlock follow : flowGraph.getNext(block)) {
                    out.addAll(activeVarIn.get(follow));
                }
                
                HashSet<Value> in = activeVarIn.get(block);
                HashSet<Value> def = defs.get(block);
                int beforeInSize = in.size();
                for (Value val : out) {
                    if (!def.contains(val)) {
                        in.add(val);
                    }
                }
                
                if (beforeOutSize != out.size() || beforeInSize != in.size()) {
                    changeFlag = true;
                }
                
                block = block.getPrev();
            }
        }
    }
    
    // 到达定义的 gen kill 初始化
    private void calGenKill() {
        BasicBlock block = firstBlock;//前驱，从前向后
        while (block != null) {
            HashSet<INode> gen = new HashSet<>();
            HashSet<INode> kill = new HashSet<>();
            INode code = block.getLast();//从后向前遍历，gen只保留最后一个定义语句
            while (true) {
                if (code instanceof DefNode) {
                    if (!kill.contains(code)) {
                        gen.add(code);
                    }
                    kill.addAll(varDefNode.get(((DefNode) code).getDef()));
                    kill.remove(code);
                }
                if (code == block.getFirst()) {
                    break;
                }
                code = code.getPrev();
            }
            gens.put(block, gen);
            kills.put(block, kill);
            reachDefIn.put(block, new HashSet<>());
            reachDefOut.put(block, new HashSet<>(gen));//初始化in为空，out为gen
            block = block.getNext();
        }
    }
    
    // 到达定义的in out 分析
    private void calReachDefInOut() {
        boolean changeFlag = true;
        while (changeFlag) {
            changeFlag = false;
            BasicBlock block = firstBlock;
            while (block != null) {
                HashSet<INode> in = reachDefIn.get(block);
                int beforeInSize = in.size();
                for (BasicBlock prevBlock : flowGraph.getPrev(block)) {
                    in.addAll(reachDefOut.get(prevBlock));
                }
                
                HashSet<INode> kill = kills.get(block);
                HashSet<INode> out = reachDefOut.get(block);
                int beforeOutSize = out.size();
                for (INode code : in) {
                    if (!kill.contains(code)) {
                        out.add(code);
                    }
                }
                
                if (beforeInSize != in.size() || beforeOutSize != out.size()) {
                    changeFlag = true;
                }
                
                block = block.getNext();
            }
        }
    }
    
    /**
     * 传播优化
     * 去除：
     * a = b
     * d = a + c
     * 形式的代码，转化为：
     * d = b + c
     */
    private void spreadOptimize() {
        BasicBlock block = firstBlock;
        while (block != null) {
            INode node = block.getFirst();
            while (node != null) {
                if (node instanceof UseNode) {
                    ArrayList<Value> use = ((UseNode) node).getUse();
                    ArrayList<Value> newUse = new ArrayList<>();
                    for (Value oldVal : use) {
                        //oldVal是待替换的右值，可能是另一个单赋值表达式（如move）中的左值
                        if (oldVal instanceof Number || oldVal.isGlobal()) {
                            //如果是常数，则一定不会发生替换
                            //TODO 如果是全局变量，则不能发生替换，因为不知道oldVal是否通过函数调用在其他函数中变成了别的值
                            newUse.add(oldVal);
                            continue;
                        }
                        DefNode defNode = findLastDef(node, oldVal);
                        if (defNode == null) {
                            newUse.add(oldVal);
                            continue;
                        }
                        ArrayList<Value> newVals = ((UseNode) defNode).getUse();
                        if (newVals != null && newVals.size() == 1) {
                            Value newVal = newVals.get(0);
                            if (newVal instanceof Number) {
                                newUse.add(newVal);
                                changed = true;
                            } else if (!newVal.getName().equals(Return.RET_REG) && !newVal.isGlobal() &&
                                    !searchDef((INode) defNode, node, newVal, new HashSet<>())) {
                                // 返回值不能简化掉（无法判断v0什么时候被syscall赋值），全局量不确定是否在中间的函数调用中被赋值（ycr的bug）
                                newUse.add(newVal);
                                if (!oldVal.equals(newVal)) {
                                    //TODO 形参会导致出现类似 a = a的情况?
                                    changed = true;
                                }
                            } else {
                                newUse.add(oldVal);
                            }
                        } else {
                            newUse.add(oldVal);
                        }
                    }
                    ((UseNode) node).replaceOperands(newUse);
                }
                
                if (node == block.getLast()) {
                    break;
                }
                node = node.getNext();
            }
            
            block = block.getNext();
        }
        deleteUnused();// 最后删除掉多余的代码
    }
    
    //获取运行到lastNode时，useVal的最后一次定义的位置，如果可能有多个，则返回空
    private DefNode findLastDef(INode lastINode, Value useVal) {
        // 先得到 lastNode 节点处对所有变量的所有可能的def节点，即lastNode处的 in集
        BasicBlock block = flowGraph.getBlock(lastINode);
        HashSet<INode> inNodes = reachDefIn.get(block);
        INode node = block.getFirst();
        while (node != lastINode) {
            if (node instanceof DefNode) {
                Value def = ((DefNode) node).getDef();
                inNodes.removeAll(varDefNode.get(def));
                inNodes.add(node);
            }
            node = node.getNext();
        }
        // 判断是否唯一
        DefNode possibleDef = null;
        for (INode defNode : inNodes) {
            //根据上面的生成规则，一定是DefNode
            assert defNode instanceof DefNode;
            if (defNode instanceof Definition || defNode instanceof Input ||
                    defNode instanceof Move || defNode instanceof FetchParam) {
                //因为只考虑长度为1，所以二元和一元赋值似乎本就不存在?
                // load save 不能用右值替换，故不能直接用 UseNode
                assert defNode instanceof UseNode;
                Value lVal = ((DefNode) defNode).getDef();
                ArrayList<Value> rVals = ((UseNode) defNode).getUse();
                //只需要考虑变量不考虑数组，故右值的数量只可能为1
                if (rVals.size() == 1 && lVal instanceof Variable && lVal.equals(useVal)) {
                    if (possibleDef != null) {
                        return null;
                    } else {
                        possibleDef = (DefNode) defNode;
                    }
                }
            }
        }
        
        return possibleDef;
    }
    
    // dfs遍历start->end的所有路径中是否存在对var的赋值
    private boolean searchDef(INode startNode, INode endNode, Value var, HashSet<BasicBlock> searched) {
        BasicBlock curBlock = flowGraph.getBlock(startNode);
        INode node = startNode;
        searched.add(curBlock);// 访问过的基本块
        //块内查找
        while (node != endNode) {
            if (node == curBlock.getLast()) {
                break;
            }
            if (node instanceof DefNode && ((DefNode) node).getDef().equals(var)) {
                return true;// 终止条件
            }
            node = node.getNext();
        }
        if (node == endNode) {
            return false;// 终止条件
        }
        //后继块查找
        for (BasicBlock next : flowGraph.getNext(curBlock)) {
            if (!searched.contains(next) && searchDef(next.getFirst(), endNode, var, searched)) {
                return true;
            }
        }
        return false;
    }
    
    private void deleteUnused() {
        BasicBlock block = firstBlock;
        while (block != null) {
            INode node = block.getFirst();
            if (!flowGraph.getPrev(block).isEmpty() || block == firstBlock) {
                // 如果是有前驱的块或是流图的起点终点
                while (true) {
                    if (node instanceof DefNode) {
                        //只有是DefNode 的节点会因为 传播 而消除
                        boolean used = false;
                        Value def = ((DefNode) node).getDef();
                        //遍历所有基本块的use集判断是否出现过
                        for (HashSet<Value> vals : uses.values()) {
                            if (vals.contains(def)) {
                                used = true;
                                break;
                            }
                        }
                        if (!def.isGlobal() && !used) {
                            node = replaceWithNop(block, node);
//                            changed = true;
                            if (node == block.getLast()) {
                                break;
                            }
                        }
                    }
                    
                    if (node == block.getLast()) {
                        break;
                    }
                    node = node.getNext();
                }
            } else {
                //其余情况下没有前驱的块应该全部删除
                while (true) {
                    if (!(node instanceof Nop)) {
                        node = replaceWithNop(block, node);
                        changed = true;
                    }
                    if (node == block.getLast()) {
                        break;
                    }
                    node = node.getNext();
                }
            }
            
            block = block.getNext();
        }
    }
    
    private INode replaceWithNop(BasicBlock block, INode oldNode) {
        INode nop = new Nop();
        labelTable.reconnect(oldNode, nop);
        oldNode.replace(nop);
        if (oldNode == block.getFirst()) {
            block.setFirst(nop);
        }
        if (oldNode == block.getLast()) {
            block.setLast(nop);
        }
        if (firstINode == oldNode) {
            firstINode = nop;
        }
        if (lastINode == oldNode) {
            lastINode = nop;
        }
        return nop;
    }
    
    // 消除nop节点，并合并label
    private void removeNopOptimize() {
        final INode last = lastINode;
        INode node = firstINode.getNext();
        
        while (node != null) {
            if (node instanceof Nop) {
                INode cur = node;
                node = node.getNext();
                labelTable.reconnect(cur, node);
                cur.remove();
//                changed = true;
                if (cur == last) {
                    break;
                }
            } else {
                lastINode = node;
                if (node == last) {
                    break;
                }
                node = node.getNext();
            }
        }
    }
    
    // 多重跳转优化
    private void branchJumpOptimize() {
        INode node = firstINode;
        while (node != null) {
            if (node instanceof Jump) {
                String label = ((Jump) node).getLabel();
                String finalLabel = getFinalLabel(label);
                ((Jump) node).setLabel(finalLabel);
            } else if (node instanceof Branch) {
                String label = ((Branch) node).getLabel();
                String finalLabel = getFinalLabel(label);
                ((Branch) node).setLabel(finalLabel);
            }
            if (node == lastINode) {
                break;
            }
            node = node.getNext();
        }
    }
    
    private String getFinalLabel(String label) {
        INode node = labelTable.getNode(label);
        while (node instanceof Nop) {
            node = node.getNext();
        }
        if (node instanceof Jump && !((Jump) node).getLabel().equals(label)) {
            return getFinalLabel(((Jump) node).getLabel());
        } else {
            return label;
        }
    }
    
    // 常数运算优化
    private void constOptimize() {
        INode node = firstINode;
        while (node != null) {
            final INode oldNode = node;
            if (node instanceof Branch) {
                node = ((Branch) node).optimize(labelTable);
            } else if (node instanceof Jump) {
                node = ((Jump) node).optimize(labelTable);
            } else if (node instanceof BinaryOp) {
                node = ((BinaryOp) node).optimize();
            } else if (node instanceof UnaryOp) {
                node = ((UnaryOp) node).optimize();
            }
            // 发生代码替换
            if (oldNode != node) {
                changed = true;
                node = oldNode.replace(node);
                labelTable.reconnect(oldNode, node);
                if (oldNode == firstINode) {
                    firstINode = node;
                }
                if (oldNode == lastINode) {
                    lastINode = node;
                }
            }
            if (node == lastINode) {
                break;
            }
            node = node.getNext();
        }
    }
}
