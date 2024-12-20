package com.craftinginterpreters.lox;
import java.util.HashMap;
import java.util.Map;

public class LoxInstance {
    private LoxClass klass;
    // map property names to values in a HashMap
    private final Map<String, Object> fields = new HashMap<>();

    LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    Object get(Token name) {
        // first look up possible fields matching `name`
        if (fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        // if that fails, look up possible methods matching `name`
        LoxFunction method = klass.findMethod(name.lexeme);
        if (method != null) return method.bind(this);

        throw new RuntimeError(name,
                "Undefined property '" + name.lexeme + "'.");
    }

    void set(Token name, Object value) {
        // no need to check if lexeme already present,
        // as we can freely create new fields on instances
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
        return klass.name + " instance";
    }
}
