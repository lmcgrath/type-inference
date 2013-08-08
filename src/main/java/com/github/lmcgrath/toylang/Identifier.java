package com.github.lmcgrath.toylang;

public class Identifier implements Expression {

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public <R, S> R accept(ExpressionVisitor<R, S> visitor, S state) throws TypeException {
        return visitor.visitIdentifier(this, state);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
