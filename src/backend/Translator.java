package backend;

import backend.element.Address;
import backend.element.Imm;
import backend.element.LabelAddr;
import backend.element.MIPSUnit;
import backend.element.Reg;
import backend.element.RegAddr;
import backend.instruction.ICal;
import backend.instruction.J;
import backend.instruction.Jal;
import backend.instruction.Jr;
import backend.instruction.La;
import backend.instruction.Li;
import backend.instruction.Lw;
import backend.instruction.MIPSCode;
import backend.instruction.MiddleComment;
import backend.instruction.Move;
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
import middle.instruction.StackSpace;
import middle.instruction.UnaryOp;
import middle.val.Number;
import middle.val.Value;
import middle.val.Variable;

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
            last = last.insert(new MiddleComment(pointer));
            final MIPSCode lastLast = last;// 上一个中间指令的最后一条指令
            //TODO 跳转指令是否需要设置保存值?
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
        MIPSUnit op1 = getRegOrImm(code.getOp1(), forbids, true);
        MIPSUnit op2 = getRegOrImm(code.getOp2(), forbids, true);
        MIPSUnit left = getRegOrImm(code.getResult(), forbids, false);
        if (code.getOp1() instanceof middle.val.Address) {
            //左操作数是地址
            if (op2 instanceof Imm) {
                op2 = new Imm(((Imm) op2).getVal() * 4);
                MIPSCode mipsCode = new ICal(ICal.middle2MIPSBinary.get(code.getOp()), (Reg) left, (Reg) op1, (Imm) op2);
                last = last.insert(mipsCode);
            } else {
                MIPSCode calOffset = new ICal(ICal.Op.SLL, Reg.TMP, (Reg) op1, new Imm(2));
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
                MIPSCode mipsCode = new ICal(ICal.middle2MIPSBinary.get(code.getOp()), (Reg) left, (Reg) op1, (Imm) op2);
                last = last.insert(mipsCode);
            } else {
                MIPSCode binaryCode = new RCal(RCal.middle2MIPSBinary.get(code.getOp()), (Reg) left, (Reg) op1, (Reg) op2);
                last = last.insert(binaryCode);
            }
        } else {
            //op1是立即数
            if (op2 instanceof Imm) {
                int res = BinaryOp.calConst(code);
                MIPSCode mipsCode = new Li((Reg) left, new Imm(res));
                last = last.insert(mipsCode);
            } else {
                MIPSCode move = new Li(Reg.TMP, (Imm) op1);
                MIPSCode binaryCode = new RCal(RCal.middle2MIPSBinary.get(code.getOp()), (Reg) left, Reg.TMP, (Reg) op2);
                last = last.insert(move);
                last = last.insert(binaryCode);
            }
        }
    }
    
    private void transBranch() {
        //TODO
        
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
        HashMap<Reg, Value> curContext = scheduler.getCurrentContext();
        for (Reg reg : curContext.keySet()) {
            Value val = scheduler.reg2val(reg);
            //TODO 判断是否要存回去的条件?
            if (scheduler.isActive(pointer, val)) {
                last = last.insert(new Sw(reg, val2addr.get(val)));
            }
        }
        //ra一定要存
        last = last.insert(new Sw(Reg.RET_ADDR, new RegAddr(Reg.SP, 0)));// sw $ra, 0($sp)
        last = last.insert(new Jal("func_" + ((Call) pointer).getLabel()));// jal func，用func_避免和其他标签重名
        last = last.insert(new Lw(Reg.RET_ADDR, new RegAddr(Reg.SP, 0)));// lw $ra, 0($sp)
        // 恢复上下文
        for (Reg reg : curContext.keySet()) {
            Value val = scheduler.reg2val(reg);
            //TODO 判断是否要读回来的条件?
            if (scheduler.isActive(pointer, val)) {
                last = last.insert(new Lw(reg, val2addr.get(val)));
            }
        }
    }
    
    private void transDef() {
        assert pointer instanceof Definition;
        Definition def = (Definition) pointer;
        Value defVar = def.getName();
        if (defVar instanceof Variable) {
            //不是数组
            //TODO 如果是常量，可考虑优化，中间代码时直接变成数带入，不需要存储
            ArrayList<Value> initVals = def.getInitVals();
            if (!def.getInitVals().isEmpty()) {
                //没有进行初值赋值时不需要任何操作
                Value initVal = initVals.get(0);
                ArrayList<Reg> forbids = new ArrayList<>();
                MIPSUnit right = getRegOrImm(initVal, forbids, true);
                MIPSUnit left = getRegOrImm(defVar, forbids, false);
                if (initVal instanceof Number) {
                    last = last.insert(new Li((Reg) left, (Imm) right));
                } else {
                    last = last.insert(new Move((Reg) left, (Reg) right));
                }
            } else {
                RegAddr addr = (RegAddr) val2addr.get(defVar);
                for (int i = 0; i < initVals.size(); i++) {
                    Value initVal = initVals.get(i);
                    ArrayList<Reg> forbids = new ArrayList<>();
                    MIPSUnit right = getRegOrImm(initVal, forbids, true);
                    if (right instanceof Imm) {
                        // a[1] = 5;
                        last = last.insert(new Li(Reg.TMP, (Imm) right));// li $tmp, 5
                        last = last.insert(new Sw(Reg.TMP, new RegAddr(addr, i * 4)));// sw $tmp, 4($a) （4($a)实际会翻译成16($sp)这种）
                    } else {
                        last = last.insert(new Sw((Reg) right, new RegAddr(addr, i * 4)));
                    }
                }
                //TODO 是否要把定义的变量读入寄存器?
            }
        }
    }
    
    private void transExit() {
        last = last.insert(new Li(Reg.RET_VAL, new Imm(10)));
        last = last.insert(new Syscall());
    }
    
    private void transFetch() {
        //TODO
        // 似乎什么也不用做?是否需要加载到寄存器里?
    }
    
    private void transEntry() {
        //TODO 注意增加func_前缀
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
        scheduler.clear();
        FuncEntry entry = (FuncEntry) pointer;
        INode tmp = entry.getNext();
        fpSize = 4;
        //计算栈帧大小
        Stack<Value> vars = new Stack<>();
        Stack<Integer> sizes = new Stack<>();
        while (tmp != null && !(tmp instanceof FuncEntry)) {
            if (tmp instanceof StackSpace) {
                int size = ((StackSpace) tmp).getSize();
                if (size != 0) {
                    Value var = ((StackSpace) tmp).getNewVar();
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
        mipsTable.connect("func_" + entry.getLabel(), last);
    }
    
    private void transInput() {
        last = last.insert(new Li(Reg.RET_VAL, new Imm(5)));// li $v0, 5
        last = last.insert(new Syscall());// syscall
    }
    
    private void transJump() {
        Jump jump = (Jump) pointer;
        last = last.insert(new J(jump.getLabel()));
    }
    
    private void transLoad() {
        Load load = (Load) pointer;
        ArrayList<Reg> forbid = new ArrayList<>();
        MIPSUnit right = getRegOrImm(load.getAddr(), forbid, true);
        MIPSUnit left = getRegOrImm(load.getDst(), forbid, false);
        last = last.insert(new Lw((Reg) left, new RegAddr((Reg) right, 0)));
    }
    
    private void transMove() {
        middle.instruction.Move move = (middle.instruction.Move) pointer;
        ArrayList<Reg> forbids = new ArrayList<>();
        MIPSUnit src = getRegOrImm(move.getrVal(), forbids, true);
        MIPSUnit dst = getRegOrImm(move.getlVal(), forbids, false);
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
    }
    
    private void transPush() {
        pushNum++;
        PushParam push = (PushParam) pointer;
        MIPSUnit param = getRegOrImm(push.getPara(), new ArrayList<>(), true);
        if (param instanceof Imm) {
            last = last.insert(new Li(Reg.TMP, (Imm) param));
            last = last.insert(new Sw(Reg.TMP, new RegAddr(Reg.SP, -pushNum * 4)));
        } else {
            last = last.insert(new Sw((Reg) param, new RegAddr(Reg.SP, -pushNum * 4)));
        }
    }
    
    private void transReturn() {
        Return ret = (Return) pointer;
        Value retVal = ret.getRet();
        if (retVal != null) {
            MIPSUnit unit = getRegOrImm(retVal, new ArrayList<>(), true);
            if (unit instanceof Imm) {
                last = last.insert(new Li(Reg.RET_VAL, (Imm) unit));
            } else {
                last = last.insert(new Move(Reg.RET_VAL, (Reg) unit));
            }
        }
        last = last.insert(new ICal(ICal.Op.ADDIU, Reg.SP, Reg.SP, new Imm(this.fpSize)));//addiu $sp, $sp, frame size
        last = last.insert(new Jr());// jr $ra
    }
    
    private void transSave() {
        Save save = (Save) pointer;
        ArrayList<Reg> forbids = new ArrayList<>();
        MIPSUnit val = getRegOrImm(save.getSrc(), forbids, true);
        MIPSUnit addr = getRegOrImm(save.getDst(), forbids, true);//TODO 此处为何需要alloc特判?
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
        MIPSUnit src = getRegOrImm(unary.getSrc(), forbids, true);
        MIPSUnit dst = getRegOrImm(unary.getDst(), forbids, true);
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
    private MIPSUnit getRegOrImm(Value val, ArrayList<Reg> forbids, boolean load) {
        //TODO 得到mips里的运算数，可以是寄存器或立即数
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
                ret = scheduler.possibleFree(forbids);
                Value oldVal = scheduler.reg2val(ret);
                //对于非数组变量和临时变量，如果被替换时还活跃则需要存入
                if ((oldVal.isTemp() || oldVal instanceof Variable) && scheduler.isActive(pointer, oldVal)) {
                    last = last.insert(new Sw(ret, val2addr.get(oldVal)));
                }
                scheduler.replace(ret, val);
            }
            // 载入寄存器
            if (load) {
                if (val instanceof Variable) {
                    last = last.insert(new Lw(ret, val2addr.get(val)));
                } else {
                    last = last.insert(new La(ret, val2addr.get(val)));
                }
            }
        }
        
        forbids.add(ret);
        return ret;
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
            val2addr.put(def.getNewVar(), new LabelAddr(globalVal.getLabel()));
            mipsTable.addDirective(globalVal);
            pointer = pointer.getNext();
            while (pointer instanceof middle.instruction.Nop) {
                pointer = pointer.getNext();
            }
        }
    }
}
