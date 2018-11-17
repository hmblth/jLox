package lox.main;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        throw new RuntimeError(name, "Attempting to access undefined variable");
    }

    void define(Token name, Object value) {
        if(!values.containsKey(name.lexeme)) {
            values.put((String)name.lexeme, value);
        }

        // throw an error if a variable with that name already exists
        throw new RuntimeError(name, "Attempting to redefine existing variable");
    }
}
