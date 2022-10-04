package error;

public class Error implements Comparable<Error> {
    private final int line;
    private final ErrorType type;
    
    public Error(int line, ErrorType type) {
        this.line = line;
        this.type = type;
    }
    
    @Override
    public String toString() {
        return line + " " + type.getErrorCode() + "\n";
    }
    
    @Override
    public int compareTo(Error o) {
        return line - o.line;
    }
}
