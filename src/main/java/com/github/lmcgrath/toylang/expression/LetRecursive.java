package com.github.lmcgrath.toylang.expression;

import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.type.Type;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class LetRecursive implements Expression {

    private final String     name;
    private final Expression definition;
    private final Expression body;
    private final Type       type;

    @Override
    public Expression checkTypes(Scope scope) {
        return scope.scoped(letScope -> {
            Type defVarType = scope.reserveType();
            letScope.define(name, defVarType);
            letScope.specialize(defVarType);
            Expression checkedDefinition = definition.checkTypes(letScope);
            defVarType.unify(checkedDefinition.getType(), scope).orElseGet(unification -> {
                scope.error(unification);
                return null;
            });
            letScope.generify(defVarType);
            Expression checkedScope = body.checkTypes(letScope);
            return new LetRecursive(
                name,
                checkedDefinition,
                checkedScope,
                checkedScope.getType()
            );
        });
    }

    @Override
    public Type getType() {
        return type;
    }
}
