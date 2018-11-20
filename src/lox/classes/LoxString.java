package lox.classes;

import lox.errors.RuntimeError;
import lox.main.Token;

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
    public ILoxObject runUnaryOperations(Token operator) {
        switch(operator.type) {
            case BANG:
                return bool(operator);
        }

        throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme);
    }

    @Override
    public ILoxObject runBinaryOperations(Token operator, ILoxObject other) {
        switch(operator.type) {
            case PLUS:
                return add(operator, other);
            case STAR:
                return multiply(operator, other);
        }

        throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme);
    }

    private ILoxObject add(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxString(this.toString() + other.toString());
            case STRING:
                return new LoxString(this.toString() + other.toString());
            default:
                throw new RuntimeError(operator, "No overload method found for adding types " + this.getType() + " and " + other.getType());
        }
    }

    private ILoxObject multiply(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxString(multiplyString(operator, value, (double)other.getValue()));
            default:
                throw new RuntimeError(operator, "No overload method found for multiplying types " + this.getType() + " and " + other.getType());
        }
    }

    private ILoxObject bool(Token operator) {
        return new LoxBool(false);
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
