package com.github.lmcgrath.toylang;

public interface Expression {

    <R, S> R accept(ExpressionVisitor<R, S> visitor, S state) throws TypeException;
}
