package com.github.lmcgrath.toylang.expression;

import static com.github.lmcgrath.toylang.type.Types.fn;
import static com.github.lmcgrath.toylang.type.Unifications.unified;

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
        Type checkedResult = checkedFunction.getType()
            .unify(fn(checkedArgument.getType(), resultType), scope)
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
