package com.github.lmcgrath.toylang.expression;

import static com.github.lmcgrath.toylang.type.Types.fn;

import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.type.Type;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Lambda implements Expression {

    private final String     variable;
    private final Expression body;
    private final Type       type;

    @Override
    public Expression checkTypes(Scope scope) {
        return scope.scoped(lambdaScope -> {
            Type argType = scope.reserveType();
            lambdaScope.define(variable, argType);
            lambdaScope.specialize(argType);
            Expression checkedBody = body.checkTypes(lambdaScope);
            return new Lambda(variable, checkedBody, fn(argType, checkedBody.getType()));
        });
    }

    @Override
    public Type getType() {
        return type;
    }
}
