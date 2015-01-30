package com.github.lmcgrath.toylang.type;

public interface TypeScope {

    Unification bind(TypeVariable variable, Type type);

    Type expose(TypeVariable variable);

    boolean isGeneric(TypeVariable variable);

    Type reserveType();
}
