package backend;

import backend.instruction.MIPSCode;
import backend.instruction.directive.Directive;

import java.util.ArrayList;
import java.util.HashMap;

public class MIPSLabelTable {
    // .text 段的labeltable 和 .data段的labelTable
    private final ArrayList<Directive> datas = new ArrayList<>();// .data段
    private final HashMap<String, MIPSCode> label2code = new HashMap<>();
    private final HashMap<MIPSCode, ArrayList<String>> code2label = new HashMap<>();
    
    public void addDirective(Directive directive) {
        //添加新的伪指令
        datas.add(directive);
    }
    
    public ArrayList<Directive> getDirectives() {
        return datas;
    }
    
    public MIPSCode getCode(String label) {
        return label2code.get(label);
    }
    
    public ArrayList<String> getLabels(MIPSCode code) {
        return code2label.get(code);
    }
    
    public void connect(String label, MIPSCode code) {
        label2code.put(label, code);
        if (!code2label.containsKey(code)) {
            code2label.put(code, new ArrayList<>());
        }
        code2label.get(code).add(label);//TODO 插入顺序?
    }
}
