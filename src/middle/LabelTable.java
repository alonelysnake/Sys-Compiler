package middle;

import middle.instruction.INode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LabelTable {
    private int cnt = 0;//标签数
    private final Map<String, INode> label2INode = new HashMap<>();//标签和跳转到的第一条指令的对应关系
    private final Map<INode, ArrayList<String>> iNode2Label = new HashMap<>();//指令-以该指令为第一条指令的label
    
    public INode getNode(String label) {
        if (!label2INode.containsKey(label)) {
            System.err.println("未找到标签：" + label);
        }
        return label2INode.get(label);
    }
    
    public ArrayList<String> getLabels(INode iNode) {
        if (iNode == null) {
            return null;
        }
        if (!iNode2Label.containsKey(iNode)) {
            return null;
        }
        return iNode2Label.get(iNode);
    }
    
    public String createLabel(boolean isLoop, boolean isBegin) {
        if (isLoop) {
            if (isBegin) {
                return "label_loop_begin_" + cnt++;
            } else {
                return "label_loop_end_" + cnt++;
            }
        }
        return "label_" + cnt++;
    }
    
    //连接标签和对应的第一条指令
    public void connect(String label, INode target) {
        this.label2INode.put(label, target);
        if (!this.iNode2Label.containsKey(target)) {
            this.iNode2Label.put(target, new ArrayList<>());
        }
        this.iNode2Label.get(target).add(label);//TODO 是否有插入位置要求?
    }
    
    public void reconnect(INode oldTarget, INode newTarget) {
        if (iNode2Label.containsKey(oldTarget)) {
            ArrayList<String> labels = iNode2Label.get(oldTarget);
            if (!iNode2Label.containsKey(newTarget)) {
                iNode2Label.put(newTarget, new ArrayList<>());
            }
            if (labels == null) {
                return;
            }
            iNode2Label.get(newTarget).addAll(labels);
            iNode2Label.remove(oldTarget);
            labels.forEach(label -> label2INode.replace(label, newTarget));
        }
    }
    
    public void removeLabel(String label) {
        //删除多余label
        INode node = label2INode.get(label);
        if (node != null) {
            label2INode.remove(label);
            iNode2Label.get(node).remove(label);
        }
    }
    
    public void replaceINode(INode src, INode dst) {
        //TODO
    }
}
