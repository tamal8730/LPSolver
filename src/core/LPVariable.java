package core;

/**
 * Object to represent a variable. It holds the variable label and the value
 */
public record LPVariable(String label, double value) {

    @Override
    public String toString() {
        return '{' +
                label +
                " = " +
                value +
                '}';
    }

}