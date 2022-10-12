package middle.val;

public class Address extends Value {
    public Address(String name) {
        super(name);
    }
    
    @Override
    public String toString() {
        return "&" + super.toString();
    }
}
