package utils;

public abstract class LinkNode<T extends LinkNode<T>> {
    // 自定义的链表节点（禁止循环）
    // 用于表示中间代码和最终代码生成过程中，每一行（或者模块？）的节点
    // 所有中间/最终代码必须继承本类
    private T next;
    private T prev;
    private T first;
    private T last;
    
    public LinkNode() {
        next = null;
        prev = null;
    }
    
    public T getPrev() {
        return prev;
    }
    
    public T getNext() {
        return next;
    }
    
    public void setNext(T next) {
        this.next = next;
    }
    
    public void setPrev(T prev) {
        this.prev = prev;
    }
    
    @SuppressWarnings("unchecked")
    //把node插入到该节点之后，返回node原本的的尾节点
    public T insert(T node) {
        //要求 node != null
        T next = this.next;
        node.setPrev((T) this);// 保证所有节点都继承了本类
        this.next = node;
        T p = node;
        while (p.getNext() != null) {
            p = p.getNext();
        }
        p.setNext(next);
        if (next != null) {
            next.setPrev(p);
        }
        return p;
    }
    
    // 把该节点替换为另一（组）节点，返回另一（组）节点原本的末节点
    public T replace(T node) {
        /*T p = node;
        while (p.getNext() != null) {
            p = p.getNext();
        }*/
        T p = this.insert(node);
        this.remove();
        /*prev.insert(node);
        if (next != null) {
            p.insert(next);
        }*/
        return p;
    }
    
    //从链表中删除该节点
    public void remove() {
        if (prev != null) {
            if (next != null) {
                prev.setNext(next);
                next.setPrev(prev);
            } else {
                prev.setNext(null);
            }
        } else {
            if (next != null) {
                next.setPrev(null);
            }
        }
    }
}
