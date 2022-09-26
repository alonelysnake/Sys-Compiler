package syntax.decl;

import error.AnalysisState;
import syntax.SyntaxNode;

public interface InitVal extends SyntaxNode {
    boolean isConst();
}
