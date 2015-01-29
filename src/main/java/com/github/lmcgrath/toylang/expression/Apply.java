package com.github.lmcgrath.toylang.expression;

import static com.github.lmcgrath.toylang.type.TypeOperator.fn;
import static com.github.lmcgrath.toylang.unification.Unifications.unified;

import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.type.Type;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Apply implements Expression {

    private final Expression function;
    private final Expression argument;
    private final Type       type;

    @Override
    public Expression checkTypes(Scope scope) {
        Expression checkedFunction = function.checkTypes(scope);
        Expression checkedArgument = argument.checkTypes(scope);
        Type resultType = scope.reserveType();
        Type checkedResult = scope
            .unify(checkedFunction.getType(), fn(checkedArgument.getType(), resultType))
            .map(type -> unified(resultType))
            .orElseGet(u -> {
                scope.error(u);
                return resultType;
            });
        return new Apply(checkedFunction, checkedArgument, checkedResult);
    }

    @Override
    public Type getType() {
        return type;
    }
}
