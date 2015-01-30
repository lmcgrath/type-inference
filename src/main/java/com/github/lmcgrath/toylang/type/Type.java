package com.github.lmcgrath.toylang.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Type {

    public abstract Unification bind(Type type, TypeScope scope);

    public abstract boolean contains(Type type, TypeScope scope);

    @Override
    public abstract boolean equals(Object o);

    public abstract Type expose(TypeScope scope);

    public Type genericCopy(TypeScope scope) {
        return expose(scope).genericCopy(scope, new HashMap<>());
    }

    public abstract String getName();

    public abstract List<Type> getParameters();

    @Override
    public abstract int hashCode();

    @Override
    public String toString() {
        return toString_();
    }

    public Unification unify(Type target, TypeScope scope) {
        return expose(scope).unify_(target.expose(scope), scope);
    }

    protected abstract Type genericCopy(TypeScope scope, Map<Type, Type> mappings);

    protected abstract String toParenthesizedString();

    protected abstract String toString_();

    protected abstract Unification unifyWith(TypeVariable query, TypeScope scope);

    protected abstract Unification unifyWith(TypeOperator query, TypeScope scope);

    protected abstract Unification unify_(Type target, TypeScope scope);
}
