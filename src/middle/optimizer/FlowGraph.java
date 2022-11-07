package middle.optimizer;

import middle.instruction.INode;

import java.util.ArrayList;
import java.util.HashMap;

public class FlowGraph {
    //流图
    private final HashMap<BasicBlock, ArrayList<BasicBlock>> next = new HashMap<>();//后继
    private final HashMap<BasicBlock, ArrayList<BasicBlock>> prev = new HashMap<>();//前驱
    private final HashMap<INode, BasicBlock> iNode2Block = new HashMap<>();// 代码和基本块的对应关系
    
    public ArrayList<BasicBlock> getNext(BasicBlock block) {
        if (!next.containsKey(block)) {
            return new ArrayList<>();
        }
        return next.get(block);
    }
    
    public ArrayList<BasicBlock> getPrev(BasicBlock block) {
        if (!prev.containsKey(block)) {
            return new ArrayList<>();
        }
        return prev.get(block);
    }
    
    public void connectINodeBlock(INode iNode, BasicBlock block) {
        iNode2Block.put(iNode, block);
    }
    
    public BasicBlock getBlock(INode iNode) {
        return iNode2Block.get(iNode);
    }
    
    public void link(BasicBlock src, BasicBlock dst) {
        if (!next.containsKey(src)) {
            next.put(src, new ArrayList<>());
        }
        if (!prev.containsKey(dst)) {
            prev.put(dst, new ArrayList<>());
        }
        next.get(src).add(dst);
        prev.get(dst).add(src);
    }
    
    @Override
    public String toString() {
        //debug用
        StringBuilder sb = new StringBuilder();
        for (BasicBlock block : next.keySet()) {
            sb.append(block.getName()).append(": ");
            if(next.get(block)==null){
                System.out.println(block.getName());
                System.out.println("aa");
                continue;
            }
            for (BasicBlock block1:next.get(block)){
                sb.append(block1.getName()).append(" ");
            }
//            next.get(block).forEach(b -> sb.append(b.getName()).append(" "));
            sb.append("\n");
            sb.append(block);
            sb.append("---------------------------------------------------------\n");
        }
        return sb.toString();
    }
}
