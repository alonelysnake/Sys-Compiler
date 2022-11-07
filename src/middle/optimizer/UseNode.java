package middle.optimizer;

import middle.val.Value;

import java.util.ArrayList;

public interface UseNode {
    ArrayList<Value> getUse();
    
    void replaceOperands(ArrayList<Value> ops);
}
