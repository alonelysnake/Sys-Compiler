package syntax.stmt.multi;

import error.AnalysisState;
import lexer.token.Token;
import middle.BlockInfo;
import middle.LabelTable;
import middle.MiddleState;
import middle.instruction.Branch;
import middle.instruction.INode;
import middle.instruction.Jump;
import middle.instruction.Nop;
import middle.val.Number;
import syntax.exp.multi.Cond;
import syntax.stmt.Stmt;

public class WhileStmt extends JudgeStmt {
    private final Token whileSym;
    
    public WhileStmt(Token whileSym, Token leftParent, Cond condExp, Token rightParent,
                     Stmt mainStmt) {
        super(leftParent, condExp, rightParent, mainStmt);
        this.whileSym = whileSym;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        state.getInLoop();
        super.analyse(state);
        state.getOutLoop();
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        /**统一格式：
         * loop_begin_label: cond
         * branch (if jump, jump to 'loop_end_label')
         * main
         * jump 'loop_begin_label'
         * loop_end_label: loop_end
         */
        BlockInfo cond = getCondExp().generateIcode(state);
        INode first = cond.getFirst();
        INode last = cond.getLast();
        LabelTable labelTable = state.getLabelTable();
        String loopBeginLabel = labelTable.createLabel(true, true);
        labelTable.connect(loopBeginLabel, first);
        String loopEndLabel = labelTable.createLabel(true, false);
        Branch branch = new Branch(cond.getRetVal(), new Number(0), Branch.Operator.EQ, loopEndLabel);
        last = last.insert(branch);
        INode jump = new Jump(loopBeginLabel);
        last = last.insert(jump);
        INode loopEnd = new Nop();
        labelTable.connect(loopEndLabel, loopEnd);
        last = last.insert(loopEnd);
        return new BlockInfo(null, first, last);
    }
    
    @Override
    public String toString() {
        return this.whileSym + super.toString();
    }
}
