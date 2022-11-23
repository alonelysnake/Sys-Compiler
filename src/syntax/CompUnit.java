package syntax;

import error.AnalysisState;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.Exit;
import middle.instruction.Call;
import middle.instruction.INode;
import middle.instruction.Nop;
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
        INode last = new Nop();//相当于链表的first节点
        final INode first = last;
        for (Decl decl : this.globals) {
            BlockInfo declLine = decl.generateIcode(state);
            last = last.insert(declLine.getFirst());
        }
        //TODO 可以考虑把main函数放到第一个?，省去一次函数调用（但忽略主函数返回值）
        last = last.insert(new Call("func_main"));//main函数入口
        last = last.insert(new Exit());//程序退出标志（main函数运行结束返回此处）
        for (FuncDef func : this.funcs) {
            BlockInfo funcBlock = func.generateIcode(state);
            last = last.insert(funcBlock.getFirst());
            last = last.insert(new Nop());
        }
        BlockInfo mainBlock = mainFunc.generateIcode(state);
        last = last.insert(mainBlock.getFirst());
        return new BlockInfo(null, first, last);
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
