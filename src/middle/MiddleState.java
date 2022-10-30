package middle;

import symbol.SymTable;

import java.util.Stack;

public class MiddleState {
    // 进行中间代码生成时用，作为共享的状态转移
    public static int tmpCnt = 0;//临时变量计数器
    private SymTable symTable = new SymTable();//当前状态下的符号表
    private final LabelTable labelTable = new LabelTable();
    private final Stack<String> startLabels = new Stack<>();
    private final Stack<String> endLabels = new Stack<>();
    private boolean funcCreateSymTable = false;//函数创建了symtable
    
    public SymTable getSymTable() {
        return symTable;
    }
    
    public LabelTable getLabelTable() {
        return labelTable;
    }
    
    public void inLoop(String startLabel, String endLabel) {
        //进入循环时调用
        startLabels.push(startLabel);
        endLabels.push(endLabel);
    }
    
    public void outLoop() {
        //离开循环时调用
        startLabels.pop();
        endLabels.pop();
    }
    
    public String getLoopStart() {
        return startLabels.peek();
    }
    
    public String getLoopEnd() {
        return endLabels.peek();
    }
    
    public SymTable inBlock() {
        if (funcCreateSymTable) {
            funcCreateSymTable = false;
        } else {
            symTable = new SymTable(symTable);
        }
        return symTable;
    }
    
    public void outBlock() {
        symTable = symTable.getParent();
    }
    
    public boolean isGlobal() {
        return symTable.getParent() == null;
    }
    
    public void funcCreateSymTable() {
        inBlock();
        this.funcCreateSymTable = true;
    }
}
