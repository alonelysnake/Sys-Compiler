package syntax;

import error.AnalysisState;

public interface SyntaxNode {
    void analyse(AnalysisState state);
    
    //void simplyfy(AnalysisState state);
}
