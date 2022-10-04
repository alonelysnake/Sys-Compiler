package error;

import java.util.TreeSet;

public class ErrorTable {
    /**
     * 存储所有错误
     */
    
    private final TreeSet<Error> errors;
    
    public ErrorTable() {
        errors = new TreeSet<>();
    }
    
    public TreeSet<Error> getErrors() {
        return errors;
    }
    
    public boolean isEmpty() {
        return errors.isEmpty();
    }
    
    public void add(Error error) {
        errors.add(error);
    }
}
