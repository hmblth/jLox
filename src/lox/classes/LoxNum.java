package lox.classes;

import lox.errors.RuntimeError;
import lox.main.Token;

public class LoxNum implements ILoxObject {
    private final double value; // TODO: implement big nums instead of doubles
    private final LoxType type;

    public LoxNum(double value) {
        this.value = value;
        this.type = LoxType.NUMBER;
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
    public String toString() { // TODO: make using big nums, so it doesn't append .0 to ints
       if(value - (int)value == 0) {
           return Integer.toString((int)value);
       } else return Double.toString(value);
    }

    @Override
    public ILoxObject runUnaryOperations(Token operator) {
        switch(operator.type) {
            case MINUS:
                return minus(operator);
            case BANG:
                return bool(operator);
        }

        throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using type " + this.type);
    }

    @Override
    public ILoxObject runBinaryOperations(Token operator, ILoxObject other) {
        switch(operator.type) {
            case PLUS:
                return add(operator, other);
            case MINUS:
                return subtract(operator, other);
            case STAR:
                return multiply(operator, other);
            case SLASH:
                return divide(operator, other);
            case STAR_STAR:
                return power(operator, other);
            case GREATER:
            case GREATER_EQUAL:
            case LESS:
            case LESS_EQUAL:
            case BANG_EQUAL:
            case EQUAL_EQUAL:
                return compare(operator, other);
            default:
                throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using types " + this.getType() + " and " + other.getType());
        }
    }

    private ILoxObject add(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxNum((double)this.getValue() + (double)other.getValue());
            case STRING:
                return new LoxString(this.toString() + other.toString());
            default:
                throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using types " + this.getType() + " and " + other.getType());
        }
    }

    private ILoxObject subtract(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxNum((double)this.getValue() - (double)other.getValue());
            default:
                throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using types " + this.getType() + " and " + other.getType());
        }
    }

    private ILoxObject multiply(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxNum((double)this.getValue() * (double)other.getValue());
            default:
                throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using types " + this.getType() + " and " + other.getType());
        }
    }

    private ILoxObject divide(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxNum((double)this.getValue() / (double)other.getValue());
            default:
                throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using types " + this.getType() + " and " + other.getType());
        }
    }

    private ILoxObject power(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxNum(Math.pow((double)this.getValue(), (double)other.getValue()));
            default:
                throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using types " + this.getType() + " and " + other.getType());
        }
    }

    private ILoxObject compare(Token operator, ILoxObject other) {
        switch(operator.type) {
            case GREATER:
                return new LoxBool((double)this.getValue() > (double)other.getValue());
            case GREATER_EQUAL:
                return new LoxBool((double)this.getValue() >= (double)other.getValue());
            case LESS:
                return new LoxBool((double)this.getValue() < (double)other.getValue());
            case LESS_EQUAL:
                return new LoxBool((double)this.getValue() <= (double)other.getValue());
            case BANG_EQUAL:
                return new LoxBool((double)this.getValue() != (double)other.getValue());
            case EQUAL_EQUAL:
                return new LoxBool((double)this.getValue() == (double)other.getValue());
        }

        // unreachable
        return null;
    }

    private ILoxObject minus(Token operator) {
        return new LoxNum(-1 * value);
    }

    private ILoxObject bool(Token operator) {
        return new LoxBool(false);
    }
}
