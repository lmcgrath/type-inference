package com.github.lmcgrath.toylang.expression;

import java.util.Set;
import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.type.Type;
import com.github.lmcgrath.toylang.unification.Unification;

public class ExpressionBuilder {

    private final Scope      scope;
    private       Expression expression;

    public ExpressionBuilder() {
        scope = new Scope();
    }

    public Expression apply(Expression function, Expression argument) {
        return expression = new Apply(function, argument, scope.reserveType());
    }

    public Expression checkTypes() {
        return expression.checkTypes(scope);
    }

    public ExpressionBuilder define(String name, Type type) {
        scope.define(name, type);
        return this;
    }

    public Set<Unification> getErrors() {
        return scope.getErrors();
    }

    public Scope getScope() {
        return scope;
    }

    public Expression id(String name) {
        return expression = new Identifier(name, scope.reserveType());
    }

    public Expression lambda(String argument, Expression body) {
        return expression = new Lambda(argument, body, scope.reserveType());
    }

    public Expression let(String name, Expression definition, Expression body) {
        return expression = new Let(name, definition, body, scope.reserveType());
    }

    public Expression letrec(String name, Expression definition, Expression body) {
        return expression = new LetRecursive(name, definition, body, scope.reserveType());
    }

    public Type reserveType() {
        return scope.reserveType();
    }
}
