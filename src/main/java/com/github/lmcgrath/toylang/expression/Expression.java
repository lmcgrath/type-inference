package com.github.lmcgrath.toylang.expression;

import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.type.Type;

public interface Expression {

    Expression checkTypes(Scope scope);

    Type getType();
}
