package error;

import java.util.LinkedList;

public class ErrorTable {
    /**
     * 存储所有错误
     */
    
    private final LinkedList<Error> errors;
    
    public ErrorTable() {
        errors = new LinkedList<>();
    }
    
    public void add(Error error) {
        errors.addLast(error);
    }
}
