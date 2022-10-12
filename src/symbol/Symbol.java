package symbol;

import syntax.decl.BType;
import syntax.exp.multi.Exp;

import java.util.ArrayList;

public class Symbol {
    /**
     * 符号表表项
     */
    
    private final String name;//变量名
    private final boolean constFlag;//const int 还是 int
    private final BType type;//int array mat
    // 0代表a或a[]，1代表a[5]或a[][5]，2代表a[2][3]的定义（函数或变量）形式
    private ArrayList<Integer> dims;//数组每一维的维度（根据list长度和type判断是否为函数头的形参指针，如type=arr、dims.size()=0则是）
    private ArrayList<Exp> initVals;//TODO 初值如何处理?
    private ArrayList<Integer> constVals;
    private int depth;//该符号所处符号表的深度（详见符号表cnt和depth的定义）
    
    public Symbol(String name, boolean constFlag, int dim) {
        this.name = name;
        this.constFlag = constFlag;
        if (dim == 0) {
            type = BType.INT;
        } else if (dim == 1) {
            type = BType.ARR;
        } else if (dim == 2) {
            type = BType.MAT;
        } else {
            System.err.println("symbol构造：高于2维的数组");
            type = null;
        }
    }
    
    public BType getType() {
        return type;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isConst() {
        return constFlag;
    }
    
    public int getConstVal(ArrayList<Integer> dims) {
        if (dims.size() == 0) {
            return constVals.get(0);
        } else if (dims.size() == 1) {
            return constVals.get(dims.get(0));
        } else if (dims.size() == 2) {
            return constVals.get(dims.get(0) * this.dims.get(1) + dims.get(1));
        }
        System.err.println("symbol getConstVal 出现高于2维数组");
        return 0;
    }
    
    public int getDepth() {
        return depth;
    }
    
    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    //在生成中间代码时填写的符号表要确定初值和维度
    public void setInit(ArrayList<Integer> dims, ArrayList<Exp> initVals) {
        this.dims = dims;
        this.initVals = initVals;
    }
    
    //记录常量值
    public void setConstVals(ArrayList<Integer> consts) {
        this.constVals = consts;
    }
    
    public boolean isPointer() {
        //判断是否为函数的形参（如a[]是，a[1]不是）
        if (type == BType.ARR && dims.size() == 0) {
            return true;
        } else if (type == BType.MAT && dims.size() == 1) {
            return true;
        }
        if (type == BType.ARR && dims.size() != 1 || type == BType.MAT && dims.size() != 2) {
            System.err.println("不是形参指针也不是变量定义");
        }
        return false;
    }
}
