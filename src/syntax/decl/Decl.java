package syntax.decl;

import lexer.token.Token;
import syntax.BlockItem;

import java.util.Iterator;
import java.util.LinkedList;

public class Decl implements BlockItem {
    private Token constSym;
    private Token type;
    private LinkedList<Token> commas;
    private LinkedList<Def> defs;
    private Token semicolon;
    
    //常量声明
    public Decl(Token constSym, Token type, LinkedList<Token> commas, LinkedList<Def> defs, Token semicolon) {
        this.constSym = constSym;
        this.type = type;
        this.commas = commas;
        this.defs = defs;
        this.semicolon = semicolon;
    }
    
    //变量声明
    public Decl(Token type, LinkedList<Token> commas, LinkedList<Def> defs, Token semicolon) {
        this.constSym = null;
        this.type = type;
        this.commas = commas;
        this.defs = defs;
        this.semicolon = semicolon;
    }
    
    public boolean isConst() {
        return constSym != null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isConst()) {
            sb.append(this.constSym);
        }
        sb.append(type);
        Iterator<Def> defIterator = this.defs.iterator();
        Iterator<Token> commaIterator = this.commas.iterator();
        sb.append(defIterator.next());
        while (commaIterator.hasNext()) {
            sb.append(commaIterator.next());
            sb.append(defIterator.next());
        }
        sb.append(this.semicolon);
        if (isConst()) {
            sb.append("<ConstDecl>\n");
        } else {
            sb.append("<VarDecl>\n");
        }
        return sb.toString();
    }
}
