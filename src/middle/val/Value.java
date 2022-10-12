package middle.val;

public abstract class Value {
    //中间变量中的operand，可以是变量（如int a）或地址（如a[5]中的a）或立即数
    private final String name;//输出时变量用$xxx表示，地址用&xxx表示，立即数用^表示，为避免重名name最后用#加符号表depth区分
    
    public Value(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
