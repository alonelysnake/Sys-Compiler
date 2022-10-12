package middle.instruction;

public class Input extends INode {
    // syscall 5
    // @Input, dst👆
    // li $v0, 5
    // syscall
    @Override
    public String toString() {
        return "getint()\n";
    }
}
