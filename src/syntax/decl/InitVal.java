package syntax.decl;

import syntax.SyntaxNode;

public interface InitVal extends SyntaxNode {
    boolean isConst();
    
    int getMaxLine();
}
