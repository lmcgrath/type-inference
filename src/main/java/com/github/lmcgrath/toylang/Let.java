package com.github.lmcgrath.toylang;

public class Let implements Expression {

    private final String name;
    private final Expression definition;
    private final Expression scope;

    public Let(String name, Expression definition, Expression scope) {
        this.name = name;
        this.definition = definition;
        this.scope = scope;
    }

    @Override
    public <R, S> R accept(ExpressionVisitor<R, S> visitor, S state) throws TypeException {
        return visitor.visitLet(this, state);
    }

    public Expression getDefinition() {
        return definition;
    }

    public String getName() {
        return name;
    }

    public Expression getScope() {
        return scope;
    }

    @Override
    public String toString() {
        return "(let " + name + " = " + definition + " in " + scope + ")";
    }
}
