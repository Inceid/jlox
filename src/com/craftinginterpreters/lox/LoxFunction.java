package com.craftinginterpreters.lox;
import java.util.List;

class LoxFunction implements LoxCallable {
    // recall that arg declaration has fields name, params, body
    private final Stmt.Function declaration;
    private final Environment closure;
    private final boolean isInitializer;

    LoxFunction(Stmt.Function declaration, Environment closure,
                boolean isInitializer) {
        this.declaration = declaration;
        this.closure = closure;
        this.isInitializer = isInitializer;
    }

    LoxFunction bind(LoxInstance instance) {
        // perform a method binding as follows:
        //   given an instance, create a wrapper environment including the
        //   closure of the instance plus "this" bound to the object instance
        Environment environment = new Environment(closure);
        environment.define("this", instance);
        // and return the bound method with the modified closure!
        return new LoxFunction(declaration, environment, isInitializer);
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }

    @Override
    public Object call(Interpreter interpreter,
                       List<Object> arguments) {
        // summon the current global environment into our scope
        Environment environment = new Environment(closure);
        // summon each i'th parameter and bind its name to the i'th argument
        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme,
                    arguments.get(i));
        }
        try {
            // execute the body using this modified environment
            interpreter.executeBlock(declaration.body, environment);
        } catch (Return returnValue) {
            // allow empty returns within a class init() method
            if (isInitializer) return closure.getAt(0, "this");
            return returnValue.value;
        }
        // finally, if the current call is to init, return `this`
        if (isInitializer) return closure.getAt(0, "this");
        return null;
    }
}
