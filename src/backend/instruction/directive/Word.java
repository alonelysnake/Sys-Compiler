package backend.instruction.directive;

import middle.val.Value;

import java.util.ArrayList;

public class Word extends Directive {
    // .word，以list存储一串 int 型变量
    private final Value name;
    private final ArrayList<Integer> vals;
    
    public Word(Value name, ArrayList<Integer> vals) {
        this.name = name;
        this.vals = vals;
    }
    
    public String getLabel() {
        return name.getName().split("#")[0];
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getLabel());
        sb.append(": .word");
        for (int val : vals) {
            sb.append(" ");
            sb.append(val);
        }
        sb.append("\n");
        return sb.toString();
    }
}
