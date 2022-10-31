package syntax.stmt.multi;

import error.AnalysisState;
import lexer.token.Token;
import middle.BlockInfo;
import middle.LabelTable;
import middle.MiddleState;
import middle.instruction.INode;
import middle.instruction.Jump;
import middle.instruction.Nop;
import syntax.exp.multi.Cond;
import syntax.stmt.Stmt;

public class IfStmt extends JudgeStmt {
    private final Token ifSym;
    private final Token elseSym;
    private final Stmt elseStmt;
    
    public IfStmt(Token ifSym, Token leftParent, Cond condExp, Token rightParent, Stmt mainStmt,
                  Token elseSym, Stmt elseStmt) {
        super(leftParent, condExp, rightParent, mainStmt);
        this.ifSym = ifSym;
        this.elseSym = elseSym;
        this.elseStmt = elseStmt;
    }
    
    public IfStmt(Token ifSym, Token leftParent, Cond condExp, Token rightParent, Stmt mainStmt) {
        super(leftParent, condExp, rightParent, mainStmt);
        this.ifSym = ifSym;
        this.elseSym = null;
        this.elseStmt = null;
    }
    
    public boolean hasElse() {
        return this.elseSym != null;
    }
    
    @Override
    public void analyse(AnalysisState state) {
        super.analyse(state);
        if (elseStmt != null) {
            elseStmt.analyse(state);
        }
    }
    
    @Override
    public BlockInfo generateIcode(MiddleState state) {
        //TODO 此处是否要设置成固定的统一格式?
        /**统一格式：
         * cond
         * branch (if jump, jump to 'then_end_label')
         * then
         * jump 'else_end_label'
         * [else end label: ]then end label: then end
         * [else block]
         * [else end label: else end]
         *
         * 修改为：
         * cond
         * branch
         * then
         * then_end_label: then end
         *
         * cond
         * branch
         * then
         * jump 'else_end_label'
         * then_end_label: then_end
         * else
         * else_end_label: else_end
         */
        LabelTable labelTable = state.getLabelTable();
        BlockInfo cond = getCondExp().generateIcode(state);
        INode first = cond.getFirst();
        INode last = cond.getLast();//cond 的最后一条指令是短路成功后跳转到的指令
        String thenEndLabel = labelTable.createLabel(false, false);
//        Branch branch = new Branch(cond.getRetVal(), new Number(0), Branch.Operator.EQ, thenEndLabel);
//        last = last.insert(branch);
        Jump jump = new Jump(thenEndLabel);//cond的倒数第二条语句为所有条件都没满足时可达的，有满足的就跳到最后一句
        last = last.getPrev().insert(jump).getNext();
        BlockInfo thenBock = getMainStmt().generateIcode(state);
        last = last.insert(thenBock.getFirst());
        INode thenEnd = new Nop();
        labelTable.connect(thenEndLabel, thenEnd);
        if (elseStmt == null) {
            //TODO 也应该有个跳转，否则生成mips后寄存器分配会出现差异
            Jump jumpThenEnd = new Jump(thenEndLabel);
            last = last.insert(jumpThenEnd);
            last = last.insert(thenEnd);
        } else {
            String elseEndLabel = labelTable.createLabel(false, false);
            INode jumpElseEnd = new Jump(elseEndLabel);
            last = last.insert(jumpElseEnd);
            last = last.insert(thenEnd);
            BlockInfo elseBlock = elseStmt.generateIcode(state);
            last = last.insert(elseBlock.getFirst());
            INode elseEnd = new Nop();
            labelTable.connect(elseEndLabel, elseEnd);
            last = last.insert(elseEnd);
        }
        return new BlockInfo(null, first, last);
    }
    
    @Override
    public String toString() {
        if (hasElse()) {
            return this.ifSym + super.toString() + this.elseSym + this.elseStmt;
        } else {
            return this.ifSym + super.toString();
        }
    }
}
