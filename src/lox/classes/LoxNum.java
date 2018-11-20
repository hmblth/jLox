package lox.classes;

import lox.main.RuntimeError;
import lox.main.Token;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class LoxNum implements ILoxObject {
    private double value; // TODO: implement big nums instead of doubles
    private LoxType type;

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
    public String toString() {
        return Double.toString(this.value);
    }

    @Override
    public ILoxObject add(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxNum((double)this.getValue() + (double)other.getValue());
            case STRING:
                return new LoxString(this.toString() + other.toString());
            default:
                throw new RuntimeError(operator, "No overload method found for adding " + other.getType() + " to " + this.getType());
        }
    }

    @Override
    public ILoxObject subtract(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxNum((double)this.getValue() - (double)other.getValue());
            default:
                throw new RuntimeError(operator, "No overload method found for subtracting " + other.getType() + " from " + this.getType());
        }
    }

    @Override
    public ILoxObject multiply(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxNum((double)this.getValue() * (double)other.getValue());
            default:
                throw new RuntimeError(operator, "No overload method found for multiplying " + this.getType() + " by " + other.getType());
        }
    }

    @Override
    public ILoxObject divide(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxNum((double)this.getValue() / (double)other.getValue());
            default:
                throw new RuntimeError(operator, "No overload method found for dividing " + this.getType() + " by " + other.getType());
        }
    }

    @Override
    public ILoxObject power(Token operator, ILoxObject other) {
        switch(other.getType()) {
            case NUMBER:
                return new LoxNum(Math.pow((double)this.getValue(), (double)other.getValue()));
            default:
                throw new RuntimeError(operator, "No overload method found for multiplying " + this.getType() + " by " + other.getType());
        }
    }

    @Override
    public ILoxObject compare(Token operator, ILoxObject other) {
        throw new NotImplementedException();
    }

    @Override
    public ILoxObject minus(Token operator) {
        return new LoxNum(-1 * value);
    }

    @Override
    public ILoxObject bool(Token operator) {
        throw new NotImplementedException();
    }
}
