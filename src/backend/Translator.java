package backend;

import backend.element.Address;
import backend.element.Imm;
import backend.element.LabelAddr;
import backend.element.MIPSUnit;
import backend.element.Reg;
import backend.element.RegAddr;
import backend.instruction.Beq;
import backend.instruction.Bne;
import backend.instruction.ICal;
import backend.instruction.J;
import backend.instruction.Jal;
import backend.instruction.Jr;
import backend.instruction.La;
import backend.instruction.Li;
import backend.instruction.Lw;
import backend.instruction.MIPSCode;
import backend.instruction.Mfhi;
import backend.instruction.MiddleComment;
import backend.instruction.Move;
import backend.instruction.Mult;
import backend.instruction.Nop;
import backend.instruction.RCal;
import backend.instruction.Sw;
import backend.instruction.Syscall;
import backend.instruction.directive.Asciiz;
import backend.instruction.directive.Directive;
import backend.instruction.directive.Word;
import backend.schedule.Scheduler;
import middle.LabelTable;
import middle.instruction.BinaryOp;
import middle.instruction.Branch;
import middle.instruction.Call;
import middle.instruction.Definition;
import middle.instruction.Exit;
import middle.instruction.FetchParam;
import middle.instruction.FuncEntry;
import middle.instruction.INode;
import middle.instruction.Input;
import middle.instruction.Jump;
import middle.instruction.Load;
import middle.instruction.Print;
import middle.instruction.PushParam;
import middle.instruction.Return;
import middle.instruction.Save;
import middle.instruction.DefNode;
import middle.instruction.UnaryOp;
import middle.val.Number;
import middle.val.Value;
import middle.val.Variable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Translator {
    private INode pointer;
    private final MIPSCode first;
    private MIPSCode last;
    private final Scheduler scheduler;
    private final MIPSLabelTable mipsTable = new MIPSLabelTable();
    private final LabelTable labelTable;
    private final HashMap<Value, Address> val2addr = new HashMap<>();
    private final ArrayList<Value> pointers = new ArrayList<>();//函数形参
    private int fpSize = 0;
    private int pushNum = 0;// push时的参数个数，翻译完函数调用语句后清零。
    private int strNum = 0;// 字符串个数
    
    public Translator(INode pointer, Scheduler scheduler, LabelTable labelTable) {
        this.pointer = pointer;
        this.last = new Nop();
        this.first = last;
        this.scheduler = scheduler;
        this.labelTable = labelTable;
    }
    
    public String translate() {
        //.data处理
        globalInit();
        //.text处理
        while (pointer != null) {
//            last = last.insert(new MiddleComment(pointer));
            final MIPSCode lastLast = last;// 上一个中间指令的最后一条mips指令
            //TODO 跳转指令需要保存寄存器的值，否则对于循环会出现反复加载而没有存入、或是在未进入的分支加载而此分支未加载的情况
            //TODO 被跳转到的指令（有标签的）是否需要设置保存?
            if (pointer instanceof BinaryOp) {
                transBinaryOp();
            } else if (pointer instanceof Branch) {
                transBranch();
            } else if (pointer instanceof Call) {
                transCall();
            } else if (pointer instanceof Definition) {
                transDef();
            } else if (pointer instanceof Exit) {
                transExit();
            } else if (pointer instanceof FetchParam) {
                transFetch();
            } else if (pointer instanceof FuncEntry) {
                transEntry();
            } else if (pointer instanceof Input) {
                transInput();
            } else if (pointer instanceof Jump) {
                transJump();
            } else if (pointer instanceof Load) {
                transLoad();
            } else if (pointer instanceof middle.instruction.Move) {
                transMove();
            } else if (pointer instanceof middle.instruction.Nop) {
                transNop();
            } else if (pointer instanceof Print) {
                transPrint();
            } else if (pointer instanceof PushParam) {
                transPush();
            } else if (pointer instanceof Return) {
                transReturn();
            } else if (pointer instanceof Save) {
                transSave();
            } else if (pointer instanceof UnaryOp) {
                transUnaryOp();
            } else {
                System.err.println(pointer);
                System.err.println("未知中间代码");
            }
            MIPSCode labelCode = lastLast.getNext();
            if (!(pointer instanceof FuncEntry)) {
                ArrayList<String> labels = labelTable.getLabels(pointer);
                if (labels != null) {
                    labels.forEach(label -> mipsTable.connect(label, labelCode));
                }
            }
            //TODO 为避免跳转导致寄存器分配失败，因此对于下一条中间代码是label指向的指令的需要适当写入
            //可参考jumpClear函数
            ArrayList<String> labels = labelTable.getLabels(pointer.getNext());
            if (labels != null && !labels.isEmpty()) {
                HashMap<Reg, Value> context = new HashMap<>(scheduler.getCurrentContext(pointer));
                for (Reg reg : context.keySet()) {
                    Value val = context.get(reg);
                    Address addr = val2addr.get(val);
                    if (val instanceof Variable) {
                        if (val.isGlobal() ||
                                (scheduler.isActive(pointer, val) && !scheduler.isGlobal(reg))) {
                            last = last.insert(new Sw(reg, addr));
                        }
                    }
                    if (addr instanceof LabelAddr) {
                        scheduler.free(reg);
                    } else {
                        if (!scheduler.isGlobal(reg)) {
                            scheduler.free(reg);
                        }
                    }
                }
            }
            pointer = pointer.getNext();
            
        }
        StringBuilder sb = new StringBuilder();
        sb.append(".data\n");
        for (Directive directive : mipsTable.getDirectives()) {
            sb.append(directive);
        }
        sb.append(".text\n");
        MIPSCode code = first;
        while (code != null) {
            ArrayList<String> labels = mipsTable.getLabels(code);
            if (labels != null) {
                labels.forEach(label -> sb.append(label + ":\n"));
            }
            sb.append(code);
            code = code.getNext();
        }
        return sb.toString();
    }
    
    private void transBinaryOp() {
        BinaryOp code = (BinaryOp) pointer;
        ArrayList<Reg> forbids = new ArrayList<>();
        MIPSUnit op1 = getMIPSUnit(code.getOp1(), forbids, true);
        MIPSUnit op2 = getMIPSUnit(code.getOp2(), forbids, true);
        MIPSUnit left = getMIPSUnit(code.getResult(), forbids, false);
        //TODO 乘除优化
        if (code.getOp1() instanceof middle.val.Address) {
            //左操作数是地址，此处的中间代码不应该出现乘除法，不用考虑乘除优化
            if (op2 instanceof Imm) {
                op2 = new Imm(((Imm) op2).getVal() * 4);
                MIPSCode mipsCode = new ICal(ICal.middle2MIPSBinary.get(code.getOp()), (Reg) left, (Reg) op1, (Imm) op2);
                last = last.insert(mipsCode);
            } else {
                MIPSCode calOffset = new ICal(ICal.Op.SLL, Reg.TMP, (Reg) op2, new Imm(2));
                MIPSCode binaryCode = new RCal(RCal.middle2MIPSBinary.get(code.getOp()), (Reg) left, (Reg) op1, Reg.TMP);
                last = last.insert(calOffset);
                last = last.insert(binaryCode);
            }
        } else if (code.getOp2() instanceof middle.val.Address) {
            //TODO 右操作数是地址 有可能吗?
            ;
        } else if (code.getOp1() instanceof Variable) {
            //op1是变量
            if (op2 instanceof Imm) {
                MIPSCode mipsCode;
                int number = ((Imm) op2).getVal();
                //TODO 除法优化
                /*
                设y = x/d，x>0, d>0
                令m = 2^t/d
                改为y = MFHI[x*m] >> t，即用乘法优化除法
                产生的误差为(x*(d-1)/d)>>t，要保证该值不进入mfhi，故t>=31
                实际m=m+1才成立，否则整除情况会导致误差累积正好无法满足,如3/3得到0
                还应满足m在int范围内，为满足d=2^31-1时成立，故将其替换为：1+(2^(31+t))/d-2^32，做完乘法运算后再加上x，去除2^32的误差
                由此应保证t的值在(31,32)之间，t的值为31+log_2_d向上取整
                
                对于x<0的情况，值需要+1
                对于d<0的情况，需要再取个相反数
                 */
                if (code.getOp().equals(BinaryOp.Operator.DIV)) {
                    if (number == 1) {
                        mipsCode = new Move((Reg) left, (Reg) op1);
                    } else if (number == -1) {
                        mipsCode = new RCal(RCal.Op.SUBU, (Reg) left, Reg.ZERO, (Reg) op1);
                    } /*else if (Integer.bitCount(number) == 1) {
                        //理论上不会出现
                        int pos = 0;
                        while ((number & (1 << pos)) == 0) {
                            pos++;
                        }
                        mipsCode = new ICal(ICal.Op.SRA, (Reg) left, (Reg) op1, new Imm(number));
                    } else if (Integer.bitCount(-number) == 1) {
                        //理论上不会出现
                        int pos = 0;
                        while ((-number & (1 << pos)) == 0) {
                            pos++;
                        }
                        mipsCode = new RCal(RCal.Op.SUBU, Reg.TMP, Reg.ZERO, (Reg) op1);
                        mipsCode.insert(new ICal(ICal.Op.SRA, (Reg) left, Reg.TMP, new Imm(pos)));
                    } */else {
                        // 满足t0>=1
                        int t0 = (int) Math.ceil(Math.log(Math.abs(number)) / Math.log(2));
                        int t = 31 + t0;
                        BigInteger ml = BigInteger.ONE
                                .add(BigInteger.valueOf(2).pow(t).divide(BigInteger.valueOf(Math.abs(number))))
                                .subtract(BigInteger.valueOf(2).pow(32));
                        int m = ml.intValue();
                        mipsCode = new Li(Reg.TMP, new Imm(m));
                        MIPSCode divCode = new Mult((Reg) (op1), Reg.TMP);
                        mipsCode.insert(divCode);
                        //注意此处left可能和op1共用一个寄存器
                        divCode = divCode.insert(new Mfhi(Reg.TMP));
                        divCode = divCode.insert(new RCal(RCal.Op.ADDU, Reg.TMP, Reg.TMP, (Reg) op1));
                        divCode = divCode.insert(new ICal(ICal.Op.SRA, Reg.TMP, Reg.TMP, new Imm(t0 - 1)));//31+t0和32差1
                        divCode = divCode.insert(new RCal(RCal.Op.SLT, (Reg) left, (Reg) op1, Reg.ZERO));
                        divCode = divCode.insert(new RCal(RCal.Op.ADDU, (Reg) left, (Reg) left, Reg.TMP));
                        if (number < 0) {
                            divCode = divCode.insert(new RCal(RCal.Op.SUBU, (Reg) left, Reg.ZERO, (Reg) left));
                        }
                    }
                } else if (ICal.middle2MIPSBinary.get(code.getOp()).equals(ICal.Op.SLTI) && (number > 32767 || number < -32768)) {
                    last = last.insert(new Li(Reg.TMP, ((Imm) op2)));
                    mipsCode = new RCal(RCal.Op.SLT, (Reg) left, (Reg) op1, Reg.TMP);
                } else {
                    mipsCode = new ICal(ICal.middle2MIPSBinary.get(code.getOp()), (Reg) left, (Reg) op1, (Imm) op2);
                }
                last = last.insert(mipsCode);
            } else {
                MIPSCode binaryCode = new RCal(RCal.middle2MIPSBinary.get(code.getOp()), (Reg) left, (Reg) op1, (Reg) op2);
                last = last.insert(binaryCode);
            }
        } else {
            //op1是立即数
            if (op2 instanceof Imm) {
                //op2也是立即数，优化后不应该出现这种情况
                int res = BinaryOp.calConst(code);
                MIPSCode mipsCode = new Li((Reg) left, new Imm(res));
                last = last.insert(mipsCode);
            } else {
                //TODO addiu不用li
                MIPSCode move = new Li(Reg.TMP, (Imm) op1);
                MIPSCode binaryCode = new RCal(RCal.middle2MIPSBinary.get(code.getOp()), (Reg) left, Reg.TMP, (Reg) op2);
                last = last.insert(move);
                last = last.insert(binaryCode);
            }
        }
        //TODO 伪指令优化
    }
    
    private void transBranch() {
        //TODO 跳转指令需要保存寄存器的值，否则对于循环会出现反复加载而没有存入、或是在未进入的分支加载而此分支未加载的情况
        jumpClear();
        
        Branch branch = (Branch) pointer;
        ArrayList<Reg> forbids = new ArrayList<>();
        MIPSUnit left = getMIPSUnit(branch.getLeft(), forbids, true);
        // MIPSUnit right = getRegOrImm(branch.getRight(), forbids, true); 此处在中间代码处固定为0
        Branch.Operator op = branch.getOp();
        if (!(op.equals(Branch.Operator.EQ) || op.equals(Branch.Operator.NEQ))) {
            System.err.println("translator: 中间代码branch不应有除eq和neq外的");
        }
        if (left instanceof Reg) {
            if (op.equals(Branch.Operator.EQ)) {
                last = last.insert(new Beq(left, Reg.ZERO, new LabelAddr(branch.getLabel())));
            } else {
                last = last.insert(new Bne(left, Reg.ZERO, new LabelAddr(branch.getLabel())));
            }
        } else {
            last = last.insert(new Li(Reg.TMP, (Imm) left));
            if (op.equals(Branch.Operator.EQ)) {
                last = last.insert(new Beq(Reg.TMP, Reg.ZERO, new LabelAddr(branch.getLabel())));
            } else {
                last = last.insert(new Bne(Reg.TMP, Reg.ZERO, new LabelAddr(branch.getLabel())));
            }
        }
    }
    
    private void transCall() {
        /*
        store regs
        store ra
        push args
        ************************************************* <-- last sp
        ...                                               <-- store regs to their address
        ************************************************* <-- sp, store $ra here
        ************************************************* <-- sp-4, store parm1 here
        ...
         */
        //每次调用函数前先初始化push计数器
        pushNum = 0;
        //保存当前上下文
        HashMap<Reg, Value> curContext = scheduler.getCurrentContext(pointer);
        for (Reg reg : curContext.keySet()) {
            Value val = scheduler.reg2val(reg);
            //TODO 判断是否要存回去的条件?
            //如果val的值是地址型，则val为活跃temp时回存（非temp为形参指针，值不会变）
            if (val instanceof Variable) {
                if (scheduler.isActive(pointer, val) || val.isGlobal()) {
                    last = last.insert(new Sw(reg, val2addr.get(val)));
                }
            } else if (val.isTemp() && scheduler.isActive(pointer, val)) {
                //地址型变量，非临时变量说明是局部数组或全局变量label，均为常值不用回存
                last = last.insert(new Sw(reg, val2addr.get(val)));
            }
        }
        //未优化时的操作
//        for (Reg reg : curContext.keySet()) {
//            Value val = scheduler.reg2val(reg);
//            //TODO 判断是否要存回去的条件?
//            //如果val的值是地址型，则val为活跃temp时回存（非temp为形参指针，值不会变）
//            if (val instanceof Variable) {
//                if (scheduler.isActive(pointer, val) && scheduler.isGlobal(reg) || val.isGlobal()) {
//                    last = last.insert(new Sw(reg, val2addr.get(val)));
//                }
//            } else if (val.isTemp()) {
//                //如果变量是临时的，则一定作为左值出现过，在栈中有地址
//                //TODO 如果此时在函数中，如何处理形参指针? 按照文法规定形参不能被赋值，故无需存储
//                last = last.insert(new Sw(reg, val2addr.get(val)));
//            }
//        }
        //ra一定要存
        last = last.insert(new Sw(Reg.RET_ADDR, new RegAddr(Reg.SP, 0)));// sw $ra, 0($sp)
        last = last.insert(new Jal(((Call) pointer).getLabel()));// jal func，用func_避免和其他标签重名
        last = last.insert(new Lw(Reg.RET_ADDR, new RegAddr(Reg.SP, 0)));// lw $ra, 0($sp)
        // TODO 是否要恢复上下文?
        for (Reg reg : curContext.keySet()) {
            Value val = scheduler.reg2val(reg);
            //TODO 所有active的理论上都应该恢复
            if (val instanceof Variable) {
                if (scheduler.isActive(pointer, val) || val.isGlobal()) {
                    last = last.insert(new Lw(reg, val2addr.get(val)));
                }
            } else {
                if (scheduler.isActive(pointer, val)) {
                    if (val.isTemp() || pointers.contains(val)) {
                        //除了临时变量外，指针也需要读入
                        last = last.insert(new Lw(reg, val2addr.get(val)));
                    } else {
                        last = last.insert(new La(reg, val2addr.get(val)));
                    }
                }
            }
        }
        // 未优化时的操作
//        for (Reg reg : curContext.keySet()) {
//            Value val = scheduler.reg2val(reg);
//            //TODO 判断是否要读回来的条件?
//            if (val instanceof Variable) {
//                if (scheduler.isActive(pointer, val) || val.isGlobal()) {
//                    last = last.insert(new Lw(reg, val2addr.get(val)));
//                }
//            } else {
//                if (val.isTemp() || pointers.contains(val)) {
//                    //除了临时变量外，指针也需要读入
//                    last = last.insert(new Lw(reg, val2addr.get(val)));
//                } else {
//                    last = last.insert(new La(reg, val2addr.get(val)));
//                }
//            }
//        }
    }
    
    private void transDef() {
        assert pointer instanceof Definition;
        Definition def = (Definition) pointer;
        Value defVar = def.getName();
        final MIPSCode lastCode = last;
        if (defVar instanceof Variable) {
            //不是数组
            // 如果是常量，中间代码时直接变成数带入，不需要存储
            ArrayList<Value> initVals = def.getInitVals();
            if (!initVals.isEmpty()) {
                //没有进行初值赋值时不需要任何操作
                Value initVal = initVals.get(0);
                ArrayList<Reg> forbids = new ArrayList<>();
                MIPSUnit right = getMIPSUnit(initVal, forbids, true);
                MIPSUnit left = getMIPSUnit(defVar, forbids, false);
                if (initVal instanceof Number) {
                    last = last.insert(new Li((Reg) left, (Imm) right));
                } else {
                    last = last.insert(new Move((Reg) left, (Reg) right));
                }
            }
            //不允许只声明但未定义的变量作为右值，因此在use前一定有一次def，代码优化后不会出现“在分支一定义，分支二使用”导致的未导入但编译器以为在寄存器堆中情况。
        } else {
            RegAddr addr = (RegAddr) val2addr.get(defVar);
            ArrayList<Value> initVals = def.getInitVals();
            if (!initVals.isEmpty()) {
                for (int i = 0; i < initVals.size(); i++) {
                    Value initVal = initVals.get(i);
                    ArrayList<Reg> forbids = new ArrayList<>();
                    MIPSUnit right = getMIPSUnit(initVal, forbids, true);
                    if (right instanceof Imm) {
                        // a[1] = 5;
                        last = last.insert(new Li(Reg.TMP, (Imm) right));// li $tmp, 5
                        last = last.insert(new Sw(Reg.TMP, new RegAddr(addr, i * 4)));// sw $tmp, 4($a) （4($a)实际会翻译成16($sp)这种）
                    } else {
                        last = last.insert(new Sw((Reg) right, new RegAddr(addr, i * 4)));
                    }
                }
            }
            //将数组基地址读入寄存器 代码优化后可能出现“在分支一定义，分支二使用”导致的未导入但编译器以为在寄存器堆中情况。
            getMIPSUnit(defVar, new ArrayList<>(), true);
        }
        if (lastCode == last) {
            last = last.insert(new Nop());// 标签的挂载对象
        }
    }
    
    private void transExit() {
        last = last.insert(new Li(Reg.RET_VAL, new Imm(10)));
        last = last.insert(new Syscall());
    }
    
    private void transFetch() {
        //TODO 加载参数到寄存器，此处会造成函数调用其他函数时参数的堆积与反复 lw sw，考虑scheduler的globalLoad保存策略？
        // 若使用def use 进行分析，则需要把绑定全局寄存器的变量加载到寄存器里
        pointers.add(((FetchParam) pointer).getPara());
        FetchParam fetch = (FetchParam) pointer;
        getMIPSUnit(fetch.getPara(), new ArrayList<>(), true);
    }
    
    private void transEntry() {
        /*
        func_entry:
        addiu $sp, $sp, -frame size
        body code
        -------------------------------------------- <-- old sp, store ra here
        param 1
        param 2
        ...
        var 1
        var 2
        ...
        the last var
        -------------------------------------------- <-- new sp
         */
        FuncEntry entry = (FuncEntry) pointer;
        pointers.clear();
        scheduler.funcCall(entry.getLabel());
        INode tmp = entry.getNext();
        fpSize = 4;
        //计算栈帧大小
        Stack<Value> vars = new Stack<>();
        Stack<Integer> sizes = new Stack<>();
        while (tmp != null && !(tmp instanceof FuncEntry)) {
            if (tmp instanceof DefNode) {
                int size = ((DefNode) tmp).getSize();
                if (size != 0) {
                    Value var = ((DefNode) tmp).getDef();
                    vars.push(var);
                    sizes.push(size);
                    fpSize += 4 * size;
                }
            }
            tmp = tmp.getNext();
        }
        //栈帧大小计算完成，移动sp指针指令
        last = last.insert(new ICal(ICal.Op.ADDIU, Reg.SP, Reg.SP, new Imm(-fpSize)));//addiu $sp, $sp, -frame size
        //映射所有变量和地址
        int offset = 4;// 留出$ra的空间
        while (!vars.isEmpty()) {
            Value var = vars.peek();
            int size = sizes.peek();
            val2addr.put(var, new RegAddr(Reg.SP, offset));
            offset += size * 4;
            vars.pop();
            sizes.pop();
        }
        mipsTable.connect(entry.getLabel(), last);
    }
    
    private void transInput() {
        last = last.insert(new Li(Reg.RET_VAL, new Imm(5)));// li $v0, 5
        last = last.insert(new Syscall());// syscall
    }
    
    private void transJump() {
        //TODO 应该和branch保持一致?
        jumpClear();
        
        Jump jump = (Jump) pointer;
        last = last.insert(new J(jump.getLabel()));
    }
    
    private void transLoad() {
        Load load = (Load) pointer;
        ArrayList<Reg> forbid = new ArrayList<>();
        MIPSUnit right = getMIPSUnit(load.getAddr(), forbid, true);
        MIPSUnit left = getMIPSUnit(load.getDst(), forbid, false);
        last = last.insert(new Lw((Reg) left, new RegAddr((Reg) right, 0)));
    }
    
    private void transMove() {
        middle.instruction.Move move = (middle.instruction.Move) pointer;
        ArrayList<Reg> forbids = new ArrayList<>();
        MIPSUnit src = getMIPSUnit(move.getrVal(), forbids, true);
        MIPSUnit dst = getMIPSUnit(move.getlVal(), forbids, false);
        if (src instanceof Imm) {
            last = last.insert(new Li((Reg) dst, (Imm) src));
        } else {
            last = last.insert(new Move((Reg) dst, (Reg) src));
        }
    }
    
    private void transNop() {
        last = last.insert(new Nop());
    }
    
    private void transPrint() {
        /*
        push first param
        ...
        push last param
        print()
        
        --------------------------------------------- <-- sp
        first param
        ...
        lsat param
         */
        final MIPSCode lastCode = last;
        String format = ((Print) pointer).getString();
        int cnt = 1;
        for (int i = format.indexOf("%d"); i >= 0; i = format.indexOf("%d")) {
            String cut = format.substring(0, i);
            format = format.substring(i + 2);
            
            if (!cut.isEmpty()) {
                String label = "string_" + strNum++;
                mipsTable.addDirective(new Asciiz(label, cut));
                last = last.insert(new La(Reg.A0, new LabelAddr(label)));// la $a0, string_
                last = last.insert(new Li(Reg.RET_VAL, new Imm(4)));// li $v0, 4
                last = last.insert(new Syscall());// syscall
            }
            //所有参数都已经push
            last = last.insert(new Lw(Reg.A0, new RegAddr(Reg.SP, -4 * cnt)));// lw $a0, -cnt*4($sp)
            last = last.insert(new Li(Reg.RET_VAL, new Imm(1)));// li $v0,1
            last = last.insert(new Syscall());// syscall
            cnt++;
        }
        if (!format.isEmpty()) {
            String label = "string_" + strNum++;
            mipsTable.addDirective(new Asciiz(label, format));
            last = last.insert(new La(Reg.A0, new LabelAddr(label)));// la $a0, string_
            last = last.insert(new Li(Reg.RET_VAL, new Imm(4)));// li $v0, 4
            last = last.insert(new Syscall());// syscall
        }
        pushNum = 0;
        if (lastCode == last) {
            last = last.insert(new Nop());// 标签的挂载对象
        }
    }
    
    private void transPush() {
        pushNum++;
        PushParam push = (PushParam) pointer;
        MIPSUnit param = getMIPSUnit(push.getPara(), new ArrayList<>(), true);
        if (param instanceof Imm) {
            last = last.insert(new Li(Reg.TMP, (Imm) param));
            last = last.insert(new Sw(Reg.TMP, new RegAddr(Reg.SP, -pushNum * 4)));
        } else {
            last = last.insert(new Sw((Reg) param, new RegAddr(Reg.SP, -pushNum * 4)));
        }
    }
    
    private void transReturn() {
        
        //TODO 考虑跳转到函数结尾统一jr?也许能减少总行数
        Return ret = (Return) pointer;
        Value retVal = ret.getRet();
        if (retVal != null) {
            MIPSUnit unit = getMIPSUnit(retVal, new ArrayList<>(), true);
            if (unit instanceof Imm) {
                last = last.insert(new Li(Reg.RET_VAL, (Imm) unit));
            } else {
                last = last.insert(new Move(Reg.RET_VAL, (Reg) unit));
            }
        }
        
        //TODO 只保存全局量应该就行
        jumpClear();
        last = last.insert(new ICal(ICal.Op.ADDIU, Reg.SP, Reg.SP, new Imm(this.fpSize)));//addiu $sp, $sp, frame size
        last = last.insert(new Jr());// jr $ra
    }
    
    private void transSave() {
        Save save = (Save) pointer;
        ArrayList<Reg> forbids = new ArrayList<>();
        MIPSUnit addr = getMIPSUnit(save.getDst(), forbids, true);//TODO 此处为何需要alloc特判?
        MIPSUnit val = getMIPSUnit(save.getSrc(), forbids, true);// TODO 注意此处顺序不能错，否则可能出现临时变量被替换而没有保存的情况
        if (val instanceof Imm) {
            last = last.insert(new Li(Reg.TMP, (Imm) val));
            last = last.insert(new Sw(Reg.TMP, new RegAddr((Reg) addr, 0)));
        } else {
            last = last.insert(new Sw((Reg) val, new RegAddr((Reg) addr, 0)));
        }
    }
    
    private void transUnaryOp() {
        UnaryOp unary = (UnaryOp) pointer;
        UnaryOp.Operator op = unary.getOp();
        ArrayList<Reg> forbids = new ArrayList<>();
        MIPSUnit src = getMIPSUnit(unary.getSrc(), forbids, true);
        MIPSUnit dst = getMIPSUnit(unary.getDst(), forbids, false);
        if (src instanceof Imm) {
            switch (op) {
                case NEG:
                    last = last.insert(new Li((Reg) dst, new Imm(-((Imm) src).getVal())));
                    return;
                case NOT:
                    if (((Imm) src).getVal() == 0) {
                        last = last.insert(new Li((Reg) dst, new Imm(1)));
                    } else {
                        last = last.insert(new Li((Reg) dst, new Imm(0)));
                    }
            }
        } else {
            switch (op) {
                case NEG:
                    last = last.insert(new RCal(RCal.Op.SUBU, (Reg) dst, Reg.ZERO, (Reg) src));
                    return;
                case NOT:
                    last = last.insert(new RCal(RCal.Op.SEQ, (Reg) dst, Reg.ZERO, (Reg) src));
            }
        }
    }
    
    /**
     * 获得val所在的mips单元（寄存器或立即数）
     *
     * @param val
     * @param forbids: 暂不能覆盖的寄存器
     * @param load:    if true then 把val放到reg里
     * @return
     */
    private MIPSUnit getMIPSUnit(Value val, ArrayList<Reg> forbids, boolean load) {
        // 得到mips里的运算数，可以是寄存器或立即数
        // 立即数直接返回即可
        if (val instanceof Number) {
            return new Imm(((Number) val).getVal());
        }
        // $v0 可以直接返回
        if (val.getName().equals(Return.RET_REG)) {
            return Reg.RET_VAL;
        }
        
        Reg ret = scheduler.val2reg(val);
        if (ret == null) {
            //不在现有寄存器中
            ret = scheduler.alloc(val);
            if (ret == null) {
                //所有寄存器都已满
                ret = scheduler.possibleFree(pointer, forbids);
                Value oldVal = scheduler.reg2val(ret);
                //TODO 还需考虑此处逻辑
                //对于非数组的局部变量或是任意临时变量，如果被替换时还活跃则需要存入
                //对于全局变量，必须存入
                if ((oldVal.isTemp() || oldVal instanceof Variable) && scheduler.isActive(pointer, oldVal)) {
                    last = last.insert(new Sw(ret, val2addr.get(oldVal)));
                } else if ((oldVal instanceof Variable) && oldVal.isGlobal()) {
                    last = last.insert(new Sw(ret, val2addr.get(oldVal)));
                }
                scheduler.replace(ret, val);
            }
            // 载入寄存器
            if (load) {
                Address addr = val2addr.get(val);
                /*
                若val是variable，则一定是lw
                若val是addr，则根据在内存中的地址类型判断：
                    若为reg(相对sp的位移)，则根据条件输入判断是求值还是地址
                        如果是地址，则la （为a[1][1] 中读取a[1]时的结果，返回的应该是a[1]的地址）
                        如果是要save的，则lw（此处lw的是计算sw的基地址的临时寄存器存到内存的值）
                        如果是函数形参指针，则lw（值为记录的地址，而非指针本身的地址）
                        如果是临时寄存器，则lw（和save的原因相仿）
                    若为label（绝对地址，即全局数组），则一定la（如果是根据偏移量lw求值则不需要加载到寄存器这一步）
                 */
                if (val instanceof Variable) {
                    last = last.insert(new Lw(ret, addr));
                } else {
                    if (addr instanceof RegAddr && (val.isTemp() || pointers.contains(val))) {
                        last = last.insert(new Lw(ret, addr));
                    } else {
                        last = last.insert(new La(ret, addr));
                    }
                }
            }
        }
        
        forbids.add(ret);
        return ret;
    }
    
    /**
     * 当遇到beq、jr、j指令时，要根据此时寄存器的分配情况将寄存器的值存回内存栈，以及释放不必要的寄存器
     */
    private void jumpClear() {
        HashMap<Reg, Value> context = new HashMap<>(scheduler.getCurrentContext(pointer));
        for (Reg reg : context.keySet()) {
            Value val = context.get(reg);
            Address addr = val2addr.get(val);
            //TODO 此处做了些魔改
            // 只有变量需要存储，数组指针不需要
            if (val instanceof Variable) {
                if (val.isGlobal() ||
                        ((pointer.getPrev() == null || scheduler.isActive(pointer.getPrev(), val)) && !scheduler.isGlobal(reg))) {
                    //TODO 对于变量Variable，如果是存在临时寄存器里的活跃变量（或全局变量），则应该存回去，并释放寄存器
                    last = last.insert(new Sw(reg, val2addr.get(val)));
                }
            }
            // 释放无用的寄存器
            if (addr instanceof LabelAddr) {
                //说明是全局变量，一定存回了内存，必须释放（如果两个分支一个加载到寄存器了一个没加载，不释放会导致后续代码需要加载到寄存器但不加载）
                scheduler.free(reg);
            } else {
                //局部变量 临时变量的释放取决于是否占据了全局寄存器，未占据的可以释放
                if (!scheduler.isGlobal(reg)) {
                    scheduler.free(reg);
                }
            }
        }
    }
    
    private void globalInit() {
        // 提取全局变量，生成 .word 伪指令并把value存到addrmap里
        while (pointer instanceof middle.instruction.Nop) {
            pointer = pointer.getNext();
        }
        while (pointer instanceof Definition) {
            Definition def = (Definition) pointer;
            ArrayList<Integer> inits = new ArrayList<>();
            for (Value val : def.getInitVals()) {
                assert val instanceof Number;
                inits.add(((Number) val).getVal());
            }
            int size = def.getSize() - inits.size();
            for (int i = 0; i < size; i++) {
                inits.add(0);
            }
            Word globalVal = new Word(def.getName(), inits);
            val2addr.put(def.getDef(), new LabelAddr(globalVal.getLabel()));
            mipsTable.addDirective(globalVal);
            pointer = pointer.getNext();
            while (pointer instanceof middle.instruction.Nop) {
                pointer = pointer.getNext();
            }
        }
    }
}
