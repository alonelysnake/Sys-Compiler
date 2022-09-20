package syntax.decl;

import lexer.token.Ident;
import lexer.token.Token;
import syntax.BlockItem;

import java.util.Iterator;
import java.util.LinkedList;

public class Decl implements BlockItem {
    private final Token constSym;
    private final Token type;
    private final LinkedList<Token> commas;
    private final LinkedList<Def> defs;
    private final Token semicolon;
    
    //常量声明
    public Decl(Token constSym, Token type,
                LinkedList<Token> commas, LinkedList<Def> defs, Token semicolon) {
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
    
    //获取某一行的变量/常变量声明中声明的所有量的名字（以Ident为元素），建立符号表与错误处理
    public LinkedList<Ident> getIdentNames() {
        LinkedList<Ident> names = new LinkedList<>();
        defs.forEach(def -> names.addLast(def.getName()));
        return names;
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
