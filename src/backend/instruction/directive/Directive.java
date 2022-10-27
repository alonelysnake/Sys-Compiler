package backend.instruction.directive;

import backend.instruction.MIPSCode;

public class Directive extends MIPSCode {
    //伪指令，本次只需要考虑全局变量和字符串
    //格式： a: .word 1,2
    private String label;
}
