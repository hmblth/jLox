package lox.classes;

import lox.main.Token;

public interface ILoxObject {
    Object getValue();
    LoxType getType();
    String toString();

    ILoxObject runUnaryOperations(Token operator);
    ILoxObject runBinaryOperations(Token operator, ILoxObject other);
}
