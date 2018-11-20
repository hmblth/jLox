package lox.classes;

import lox.main.Token;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class LoxBool implements ILoxObject {
    private boolean value;
    private LoxType type;

    public LoxBool(boolean value) {
        this.value = value;
        this.type = LoxType.BOOLEAN;
    }

    @Override
    public Object getValue() {
        throw new NotImplementedException();
    }

    @Override
    public LoxType getType() {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }

    @Override
    public ILoxObject add(Token operator, ILoxObject other) {
        throw new NotImplementedException();
    }

    @Override
    public ILoxObject subtract(Token operator, ILoxObject other) {
        throw new NotImplementedException();
    }

    @Override
    public ILoxObject multiply(Token operator, ILoxObject other) {
        throw new NotImplementedException();
    }

    @Override
    public ILoxObject divide(Token operator, ILoxObject other) {
        throw new NotImplementedException();
    }

    @Override
    public ILoxObject power(Token operator, ILoxObject other) {
        throw new NotImplementedException();
    }

    @Override
    public ILoxObject compare(Token operator, ILoxObject other) {
        throw new NotImplementedException();
    }

    @Override
    public ILoxObject minus(Token operator) {
        throw new NotImplementedException();
    }

    @Override
    public ILoxObject bool(Token operator) {
        throw new NotImplementedException();
    }
}
