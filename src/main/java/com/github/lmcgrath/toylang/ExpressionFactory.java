package com.github.lmcgrath.toylang;

public final class ExpressionFactory {

    public static Apply apply(Expression function, Expression argument) {
        return new Apply(function, argument);
    }

    public static Identifier id(String name) {
        return new Identifier(name);
    }

    public static Lambda lambda(String variable, Expression body) {
        return new Lambda(variable, body);
    }

    public static Let let(String name, Expression expression, Expression scope) {
        return new Let(name, expression, scope);
    }

    public static LetRecursive letrec(String name, Expression expression, Expression scope) {
        return new LetRecursive(name, expression, scope);
    }

    private ExpressionFactory() {
        // intentionally empty
    }
}
