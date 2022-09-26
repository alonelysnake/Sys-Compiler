package error;

public class Error {
    private final int line;
    private final ErrorType type;
    
    public Error(int line, ErrorType type) {
        this.line = line;
        this.type = type;
    }
}
