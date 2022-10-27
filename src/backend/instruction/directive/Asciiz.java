package backend.instruction.directive;

public class Asciiz extends Directive {
    // 自动带'\0'的字符串
    private final String label;// 该全局量的名字
    private final String str;
    
    public Asciiz(String label, String str) {
        this.label = label;
        this.str = str;
    }
    
    @Override
    public String toString() {
        return label + ": .asciiz \"" + str + "\"\n";
    }
}
