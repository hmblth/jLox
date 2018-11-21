package lox.main;

import lox.classes.ILoxObject;
import lox.errors.RuntimeError;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    final Environment enclosing;
    private final Map<String, ILoxObject> values = new HashMap<>();

    Environment() {
        enclosing = null;
    }

    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    ILoxObject get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name, "Attempting to access undefined variable");
    }

    void assign(Token name, ILoxObject value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variables '" + name.lexeme + "'");
    }

    void define(Token name, ILoxObject value) {
        if(!values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        // throw an error if a variable with that name already exists
        throw new RuntimeError(name, "Attempting to redefine existing variable");
    }
}
