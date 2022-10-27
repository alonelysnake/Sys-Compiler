package backend.instruction;

public class Syscall extends MIPSCode {
    @Override
    public String toString() {
        return "syscall\n";
    }
}
