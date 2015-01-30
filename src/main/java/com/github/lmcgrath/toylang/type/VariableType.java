package com.github.lmcgrath.toylang.type;

import static com.github.lmcgrath.toylang.type.Unifications.unified;

import java.util.Map;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class VariableType extends Type {

    private final String name;

    VariableType(String name) {
        this.name = name;
    }

    @Override
    public Unification bind(Type type, TypeScope scope) {
        return scope.bind(this, type);
    }

    @Override
    public boolean contains(Type type, TypeScope scope) {
        return equals(type.expose(scope));
    }

    @Override
    public Type expose(TypeScope scope) {
        return scope.expose(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return toString_();
    }

    @Override
    protected Type genericCopy(TypeScope scope, Map<Type, Type> mappings) {
        if (scope.isGeneric(this)) {
            if (!mappings.containsKey(this)) {
                mappings.put(this, scope.reserveType());
            }
            return mappings.get(this);
        } else {
            return this;
        }
    }

    @Override
    protected String toParenthesizedString() {
        return toString_();
    }

    @Override
    protected String toString_() {
        return name;
    }

    @Override
    protected Unification unifyWith(ProductType query, TypeScope scope) {
        return unifyVariable(query, scope);
    }

    @Override
    protected Unification unifyWith(VariableType query, TypeScope scope) {
        if (equals(query)) {
            return unified(this);
        } else {
            return bind(query, scope);
        }
    }

    @Override
    protected Unification unifyWith(SumType query, TypeScope scope) {
        return unifyVariable(query, scope);
    }

    @Override
    protected Unification unifyWith(FunctionType query, TypeScope scope) {
        return unifyVariable(query, scope);
    }

    @Override
    protected Unification unify_(Type target, TypeScope scope) {
        return target.unifyWith(this, scope);
    }
}
