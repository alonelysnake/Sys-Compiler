package syntax;

import error.AnalysisState;
import middle.BlockInfo;
import middle.MiddleState;
import syntax.decl.Decl;
import syntax.func.FuncDef;
import syntax.func.MainFunc;

import java.util.LinkedList;

public class CompUnit implements SyntaxNode {
    private final LinkedList<Decl> globals;
    private final LinkedList<FuncDef> funcs;
    private final MainFunc mainFunc;
    
    public CompUnit(LinkedList<Decl> globals, LinkedList<FuncDef> funcs, MainFunc mainFunc) {
        this.globals = globals;
        this.funcs = funcs;
        this.mainFunc = mainFunc;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        for (Decl decl : globals) {
            decl.analyse(state);
        }
        for (FuncDef func : funcs) {
            func.analyse(state);
        }
        mainFunc.analyse(state);
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO
        return null;
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
