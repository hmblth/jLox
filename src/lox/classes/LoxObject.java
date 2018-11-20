package lox.classes;

import lox.errors.RuntimeError;
import lox.main.Token;

public class LoxObject implements ILoxObject {
    public static ILoxObject calculate(Token operator, ILoxObject[] args) {
        if(args.length == 1) {
            return args[0].runUnaryOperations(operator);
        } else if(args.length == 2) {
            return args[0].runBinaryOperations(operator, args[1]);
        }

        throw new RuntimeError(operator, "Unable to find any operators supporting " + args.length + " arguments");
    }

    private final Object value;
    private final LoxType type;

    public LoxObject(Object value) {
        this.value = value;
        this.type = LoxType.UNKNOWN;
    }

    public Object getValue() {
        return this.value;
    }

    public LoxType getType() {
        return this.type;
    }

    public ILoxObject runUnaryOperations(Token operator) {
        throw new RuntimeError(operator,"Unable to run operations on UNKNOWN type");
    }

    public ILoxObject runBinaryOperations(Token operator, ILoxObject other) {
        throw new RuntimeError(operator,"Unable to run operations on UNKNOWN type");
    }
}
