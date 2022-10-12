package syntax.exp.unary;

import symbol.SymTable;
import syntax.SyntaxNode;

public interface PrimaryUnit extends SyntaxNode {
    int getMaxLine();
    
    int calConst(SymTable symTable);
}
