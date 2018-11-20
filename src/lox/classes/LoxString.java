package lox.classes;

import lox.main.RuntimeError;
import lox.main.Token;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class LoxString implements ILoxObject {
    private String value;
    private LoxType type;

    public LoxString(String value) {
        this.value = value;
        this.type = LoxType.STRING;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public LoxType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public ILoxObject add(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxString(this.toString() + other.toString());
            case STRING:
                return new LoxString(this.toString() + other.toString());
            default:
                throw new RuntimeError(operator, "No overload method found for adding types " + this.getType() + " and " + other.getType());
        }
    }

    @Override
    public ILoxObject subtract(Token operator, ILoxObject other) {
        throw new RuntimeError(operator, "No overload method found for subtracting types " + this.getType() + " and " + other.getType());
    }

    @Override
    public ILoxObject multiply(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxString(multiplyString(operator, value, (double)other.getValue()));
            default:
                throw new RuntimeError(operator, "No overload method found for multiplying types " + this.getType() + " and " + other.getType());
        }
    }

    @Override
    public ILoxObject divide(Token operator, ILoxObject other) {
        throw new RuntimeError(operator, "No overload method found for dividing types " + this.getType() + " and " + other.getType());
    }

    @Override
    public ILoxObject power(Token operator, ILoxObject other) {
        throw new RuntimeError(operator, "No overload method found for dividing types " + this.getType() + " and " + other.getType());
    }

    @Override
    public ILoxObject compare(Token operator, ILoxObject other) {
        throw new NotImplementedException();
    }

    @Override
    public ILoxObject minus(Token operator) {
        throw new RuntimeError(operator, "No overload method found for negating type " + this.getType());
    }

    @Override
    public ILoxObject bool(Token operator) {
        throw new NotImplementedException();
    }

    private String multiplyString(Token operator, String value, double repetitions) {
        int rep = (int)repetitions;
        String ret = "";

        if (rep < 0) { // ensure we don't fuck up our loop
            throw new RuntimeError(operator, "Trying to multiply a string by a negative value");
        } else if (rep > 0) {
            for(int i = 0; i < rep; ++i) {
                ret += value;
            }
        }

        return ret;
    }
}
