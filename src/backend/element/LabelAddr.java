package backend.element;

public class LabelAddr implements Address {
    private final String label;//绝对跳转用的标签
    
    public LabelAddr(String label) {
        this.label = label;
    }
    
    @Override
    public String toString() {
        return label;
    }
}
