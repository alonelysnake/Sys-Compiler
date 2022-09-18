package syntax;

import syntax.decl.Decl;
import syntax.func.FuncDef;
import syntax.func.MainFunc;

import java.util.LinkedList;

public class CompUnit {
    private final LinkedList<Decl> globals;
    private final LinkedList<FuncDef> funcs;
    private final MainFunc mainFunc;
    
    public CompUnit(LinkedList<Decl> globals, LinkedList<FuncDef> funcs, MainFunc mainFunc) {
        this.globals = globals;
        this.funcs = funcs;
        this.mainFunc = mainFunc;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        globals.forEach(sb::append);//试试lamda表达式
        funcs.forEach(sb::append);
        sb.append(mainFunc);
        sb.append("<CompUnit>\n");
        return sb.toString();
    }
}
