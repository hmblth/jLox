package lox.classes;

import lox.main.Token;

public interface ILoxObject {
    Object getValue();
    LoxType getType();
    String toString();

    // TODO: Refactor into one calculate method
    ILoxObject add(Token operator, ILoxObject other);
    ILoxObject subtract(Token operator, ILoxObject other);
    ILoxObject multiply(Token operator, ILoxObject other);
    ILoxObject divide(Token operator, ILoxObject other);
    ILoxObject power(Token operator, ILoxObject other);

    ILoxObject compare(Token operator, ILoxObject other);

    ILoxObject minus(Token operator);
    ILoxObject bool(Token operator);
}
