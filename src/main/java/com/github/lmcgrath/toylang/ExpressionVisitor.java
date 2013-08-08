package com.github.lmcgrath.toylang;

public interface ExpressionVisitor<R, S> {

    R visitApply(Apply apply, S state) throws TypeException;

    R visitIdentifier(Identifier identifier, S state) throws TypeException;

    R visitLambda(Lambda lambda, S state) throws TypeException;

    R visitLet(Let let, S state) throws TypeException;

    R visitLetRecursive(LetRecursive let, S state) throws TypeException;
}
