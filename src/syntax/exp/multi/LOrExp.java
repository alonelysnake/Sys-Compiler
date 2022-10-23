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

public class LOrExp extends ExpList<AndExp> {
    public LOrExp(LinkedList<Token> ops, LinkedList<AndExp> units) {
        super(ops, units, "<LOrExp>\n");
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
        for (AndExp andExp : getUnits()) {
            andBlock = andExp.generateIcode(state);
            p = p.insert(andBlock.getFirst());
            Branch branch = new Branch(andBlock.getRetVal(), new Number(0), Branch.Operator.NEQ, endLabel);
            p = p.getPrev().insert(branch).getNext();//如果运行到branch，则一定是根据最后一个and的结果判断
        }
        p.insert(last);
        
        assert andBlock != null;
        return new BlockInfo(andBlock.getRetVal(), first, last);
    }
}
