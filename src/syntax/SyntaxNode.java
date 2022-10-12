package syntax;

import error.AnalysisState;
import middle.BlockInfo;
import middle.MiddleState;

public interface SyntaxNode {
    void analyse(AnalysisState state);
    
    BlockInfo generateIcode(MiddleState state);
    
    //void simplyfy(AnalysisState state);
}
