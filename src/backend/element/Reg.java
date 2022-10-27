package backend.element;

public enum Reg implements MIPSUnit {
    ZERO("0"),
    RET_VAL("v0"),
    RET_ADDR("ra"),
    SP("sp"),
    A0("a0"),//以上的不会被调度
    
    T0("t0"),
    T1("t1"),
    T2("t2"),
    T3("t3"),
    T4("t4"),
    T5("t5"),
    T6("t6"),
    T7("t7"),
    T8("t8"),
    T9("t9"),
    
    S0("s0"),
    S1("s1"),
    S2("s2"),
    S3("s3"),
    S4("s4"),
    S5("s5"),
    S6("s6"),
    S7("s7"),
    
    S8("gp"),
    S9("k0"),
    S10("k1"),
    S11("a1"),
    S12("a1"),
    S13("a2"),
    S14("a3"),
    
    TMP("fp");// 做更细致的运算时的临时寄存器，如求地址偏移量时的*4操作
    
    private final String name;
    
    Reg(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "$" + name;
    }
    
    public boolean needSave() {
        //TODO
        return true;
    }
}
