package error;

public enum ErrorType {
    ILLEGAL_CHAR("a"),
    REDEFINED_IDENT("b"),
    UNDEFINED_IDENT("c"),
    MISMATCH_PARA_NUM("d"),
    MISMATCH_PARA_TYPE("e"),
    MISMATCH_RETURN("f"),
    LACK_RETURN("g"),
    MODIFY_CONST("h"),
    LACK_SEMICOLON("i"),
    LACK_R_PARENT("j"),
    LACK_R_BRACKET("k"),
    MISMATCH_PRINTF("l"),
    BREAK_OR_CONTINUE_OUTSIDE_LOOP("m");
    
    private final String errorCode;
    
    ErrorType(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
