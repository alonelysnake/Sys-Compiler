package syntax.decl;

import syntax.SyntaxNode;
import syntax.exp.multi.Exp;

import java.util.ArrayList;

public interface InitVal extends SyntaxNode {
    boolean isConst();
    
    int getMaxLine();
    
    ArrayList<Exp> getInitVals();
}
