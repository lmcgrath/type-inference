package com.github.lmcgrath.toylang;

public class Apply implements Expression {

    private final Expression function;
    private final Expression argument;

    public Apply(Expression function, Expression argument) {
        this.function = function;
        this.argument = argument;
    }

    @Override
    public <R, S> R accept(ExpressionVisitor<R, S> visitor, S state) throws TypeException {
        return visitor.visitApply(this, state);
    }

    public Expression getArgument() {
        return argument;
    }

    public Expression getFunction() {
        return function;
    }

    @Override
    public String toString() {
        return "(" + function + " " + argument + ")";
    }
}
