package myreflection;

public class Example {
    public int intVal = 1;
    public byte byteVal = 2;
    public double dblValue = 3.5;
    public boolean boolVal = true;
    public char charVal = 'G';

    @Override
    public String toString() {
        return "Example{" +
                "intVal=" + intVal +
                ", byteVal=" + byteVal +
                ", dblValue=" + dblValue +
                ", boolVal=" + boolVal +
                ", charVal=" + charVal +
                '}';
    }
}
