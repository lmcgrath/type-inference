package com.github.lmcgrath.toylang.expression;

import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.type.Type;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Let implements Expression {

    private final String     name;
    private final Expression definition;
    private final Expression body;
    private final Type       type;

    @Override
    public Expression checkTypes(Scope scope) {
        Expression checkedDefinition = definition.checkTypes(scope);
        return scope.scoped(letScope -> {
            letScope.define(name, checkedDefinition.getType());
            Expression checkedScope = body.checkTypes(letScope);
            return new Let(name, checkedDefinition, checkedScope, checkedScope.getType());
        });
    }

    @Override
    public Type getType() {
        return type;
    }
}
