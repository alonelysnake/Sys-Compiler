package syntax.exp.multi;

import lexer.token.Ident;
import lexer.token.Token;
import middle.BlockInfo;
import middle.MiddleState;
import middle.instruction.Branch;
import middle.instruction.INode;
import middle.instruction.Nop;
import middle.val.Number;

import java.util.LinkedList;

public class AndExp extends ExpList<EqExp> {
    public AndExp(LinkedList<Token> ops, LinkedList<EqExp> units) {
        super(ops, units, "<LAndExp>\n");
    }
    
    @Override
    public LinkedList<Ident> getNames() {
        LinkedList<Ident> names = new LinkedList<>();
        getUnits().forEach(unit -> names.addAll(unit.getNames()));
        return names;
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        final INode first = new Nop();
        final INode last = new Nop();
        String endLabel = state.getLabelTable().createLabel(false, false);
        state.getLabelTable().connect(endLabel, last);
        INode p = first;
        BlockInfo andBlock = null;
        for (EqExp exp : getUnits()) {
            andBlock = exp.generateIcode(state);
            p = p.insert(andBlock.getFirst());
            Branch branch = new Branch(andBlock.getRetVal(), new Number(0), Branch.Operator.EQ, endLabel);
            p = p.insert(branch);
        }
        p.remove();//交给or判断最后一个元素（如果运行到上一级or的branch，则形式为1&&1&&last，只取决于最后一个元素）
        p.insert(last);
        
        assert andBlock != null;
        return new BlockInfo(andBlock.getRetVal(), first, last);
    }
}
