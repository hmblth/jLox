package lox.classes;

import lox.errors.RuntimeError;
import lox.main.Token;

public class LoxBool implements ILoxObject {
    private final boolean value;
    private final LoxType type;

    public LoxBool(boolean value) {
        this.value = value;
        this.type = LoxType.BOOLEAN;
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
        return Boolean.toString(this.value);
    }

    @Override
    public ILoxObject runUnaryOperations(Token operator) {
        switch(operator.type) {
            case BANG:
                return bool(operator);
        }

        // TODO: Rewrite error reporting
        throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using type " + this.getType());
    }

    @Override
    public ILoxObject runBinaryOperations(Token operator, ILoxObject other) {
        throw new RuntimeError(operator, "Unable to find a method for operator " + operator.lexeme + " using types " + this.getType() + " and " + other.getType());
    }

    private ILoxObject bool(Token operator) {
        return new LoxBool(!value);
    }
}
