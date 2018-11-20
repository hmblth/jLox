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

        throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using type " + type);
    }

    @Override
    public ILoxObject runBinaryOperations(Token operator, ILoxObject other) {
        switch(operator.type) {
            case PLUS:
                return add(operator, other);
            case STAR:
                return multiply(operator, other);
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case BANG_EQUAL:
            case EQUAL_EQUAL:
                return compare(operator, other);
        }

        throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using types " + type + " and " + other.getType());
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

    private ILoxObject compare(Token operator, ILoxObject other) {
        int result = compareStrings(this.value, (String)other.getValue());
        switch(operator.type) {
            case GREATER:
                return new LoxBool((result == 1) ? true : false);
            case GREATER_EQUAL:
                return new LoxBool((result != -1) ? true : false);
            case LESS:
                return new LoxBool((result == -1) ? true : false);
            case LESS_EQUAL:
                return new LoxBool((result != 1) ? true : false);
            case BANG_EQUAL:
                return new LoxBool((result != 0) ? true : false);
            case EQUAL_EQUAL:
                return new LoxBool((result == 0) ? true : false);
        }

        return null;
    }

    // returns 1 if first string is alphabetically first
    //        -1 if second string is alphabetically first
    //         0 if the strings are equal
    private int compareStrings(String first, String other) {
        if(first.length() > other.length()) {
            return 1;
        } else if (first.length() < other.length()) return -1;

        for(int i = 0; i < first.length(); ++i) {
            if((int)first.charAt(i) < (int)other.charAt(i)) {
                return 1;
            } else if((int)first.charAt(i) > (int)other.charAt(i)) {
                return -1;
            }
        }

        return 0;
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
