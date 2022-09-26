package error;

import symbol.SymTable;
import syntax.func.FuncDef;

import java.util.HashMap;

public class AnalysisState {
    /**
     * 记录分析到当前节点时的各种状态
     */
    
    private SymTable curSymTable;//当前栈顶的符号表
    private final ErrorTable errorTable;//错误表
    private FuncDef curFunc;//是否在处理函数
    private int loopDepth = 0;
    
    //TODO 是否要求函数的声明顺序?即前面的函数不能调用后面的
    private final HashMap<String, FuncDef> funcs;//所有声明了的函数
    
    public AnalysisState() {
        curSymTable = new SymTable();
        errorTable = new ErrorTable();
        funcs = new HashMap<>();
    }
    
    public void addError(Error error) {
        errorTable.add(error);
    }
    
    //成功添加函数声明返回true，失败（有重名）返回false
    public boolean addFunc(FuncDef func) {
        String name = func.getName().getName();
        if (this.funcs.containsKey(name)) {
            return false;
        } else {
            funcs.put(name, func);
            return true;
        }
    }
    
    public boolean containsFunc(String name) {
        return funcs.containsKey(name);
    }
    
    public FuncDef getFunc(String name) {
        return funcs.get(name);
    }
    
    public void getInLoop() {
        loopDepth++;
    }
    
    public void getOutLoop() {
        loopDepth--;
    }
    
    public boolean isInLoop() {
        return loopDepth > 0;
    }
    
    //当前是否为全局表
    public boolean isGlobal() {
        return curSymTable.getParent() == null;
    }
    
    public void setCurFunc(FuncDef func) {
        this.curFunc = func;
    }
    
    public boolean isInFunc() {
        return curFunc != null;
    }
    
    public FuncDef getCurFunc() {
        return curFunc;
    }
    
    public SymTable getSymTable() {
        return curSymTable;
    }
    
    //弹出栈顶的符号表
    public void popSymTable() {
        curSymTable = curSymTable.getParent();
    }
    
    public void pushSymTable(SymTable newTable) {
        curSymTable = newTable;
    }
}
