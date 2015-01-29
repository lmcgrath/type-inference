package com.github.lmcgrath.toylang.expression;

import static com.github.lmcgrath.toylang.unification.Unifications.undefined;

import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.type.Type;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Identifier implements Expression {

    private final String name;
    private final Type   type;

    @Override
    public Expression checkTypes(Scope scope) {
        return new Identifier(name, scope.typeOf(name)
            .orElseGet(() -> {
                scope.error(undefined(name));
                return type;
            }));
    }

    @Override
    public Type getType() {
        return type;
    }
}
