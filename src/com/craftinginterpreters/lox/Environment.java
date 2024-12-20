package com.craftinginterpreters.lox;
import java.util.HashMap;
import java.util.Map;

class Environment {
    final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    void define(String name, Object value) {
        values.put(name, value);
    }

    Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme, value);
    }

    Environment ancestor(int distance) {
        // the ancestor of e is the environment that is d hops up the
        // environment chain from e
        Environment environment = this;
        // walk up the chain `distance` times to find our variable decl
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment; // return that environment
    }

    Object get(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        // assignment is not allowed to create a new variable
        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    // initialize global scope's environment
    Environment() {
        enclosing = null;
    }

    // create a new local scope inside the outer "enclosing" one
    Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

}
