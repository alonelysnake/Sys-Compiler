package middle.instruction;

import middle.val.Value;

public interface DefNode {
    int getSize();//返回占据栈帧的大小(以字为单位)
    
    Value getDef();//返回需要栈帧的变量(通常为左值)
}
