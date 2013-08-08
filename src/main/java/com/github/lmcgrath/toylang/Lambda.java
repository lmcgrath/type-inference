package com.github.lmcgrath.toylang;

public class Lambda implements Expression {

    private final String variable;
    private final Expression body;

    public Lambda(String variable, Expression body) {
        this.variable = variable;
        this.body = body;
    }

    @Override
    public <R, S> R accept(ExpressionVisitor<R, S> visitor, S state) throws TypeException {
        return visitor.visitLambda(this, state);
    }

    public Expression getBody() {
        return body;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public String toString() {
        return "(fn " + variable + " -> " + body + ")";
    }
}
