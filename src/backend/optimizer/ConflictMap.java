package backend.optimizer;

import middle.val.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ConflictMap {
    // 冲突图
    private HashMap<Value, HashSet<Value>> map = new HashMap<>();
    
    public int getDegree(Value value) {
        if (map.containsKey(value)) {
            return map.get(value).size();
        }
        return 0;
    }
    
    public Set<Value> getAllNodes() {
        return map.keySet();
    }
    
    //找到一个可以被着色的
    public Value getColorable(int maxDegree) {
        for (Value value : map.keySet()) {
            if (map.get(value).size() < maxDegree) {
                return value;
            }
        }
        return null;
    }
    
    public HashSet<Value> getConnectNodes(Value value) {
        return new HashSet<>(map.get(value));//TODO 为何要copy一份?
    }
    
    public void addNode(Value value) {
        if (!map.containsKey(value)) {
            map.put(value, new HashSet<>());
        }
    }
    
    public void link(Value val1, Value val2) {
        if (!map.containsKey(val1)) {
            map.put(val1, new HashSet<>());
        }
        if (val1.equals(val2)) {
            return;
        }
        if (!map.containsKey(val2)) {
            map.put(val2, new HashSet<>());
        }
        map.get(val1).add(val2);
        map.get(val2).add(val1);
    }
    
//    public void reLink(Value value, HashSet<Value>edge){
//        map.put(value, edge);
//        for()
//    }
    
    public int size() {
        return map.size();
    }
    
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    public void remove(Value value) {
        map.remove(value);
        for (Value val : map.keySet()) {
            map.get(val).remove(value);
        }
    }
    
    @Override
    public String toString() {
        return map.toString();
    }
}
