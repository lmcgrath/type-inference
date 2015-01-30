package com.github.lmcgrath.toylang.type;

public interface TypeScope {

    Unification bind(VariableType variable, Type type);

    Type expose(VariableType variable);

    boolean isGeneric(VariableType variable);

    Type reserveType();
}
