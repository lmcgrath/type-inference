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
        Type defVarType = scope.reserveType();
        Scope letScope = scope.extend();
        letScope.define(name, defVarType);
        letScope.specialize(defVarType);
        Expression checkedDefinition = definition.checkTypes(letScope);
        scope.unify(defVarType, checkedDefinition.getType()).orElseGet(unification -> {
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
    }

    @Override
    public Type getType() {
        return type;
    }
}
