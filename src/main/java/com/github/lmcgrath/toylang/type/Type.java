package com.github.lmcgrath.toylang.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.unification.Unification;

public abstract class Type {

    public abstract Unification bind(Type type);

    public abstract boolean contains(Type type);

    @Override
    public abstract boolean equals(Object o);

    public abstract Type expose();

    public Type genericCopy(Scope scope) {
        return expose().genericCopy(scope, new HashMap<>());
    }

    public abstract String getName();

    public abstract List<Type> getParameters();

    @Override
    public abstract int hashCode();

    @Override
    public String toString() {
        return toString_();
    }

    public Unification unify(Type target, Scope scope) {
        return expose().unify_(target.expose(), scope);
    }

    protected abstract Type genericCopy(Scope scope, Map<Type, Type> mappings);

    protected abstract String toParenthesizedString();

    protected abstract String toString_();

    protected abstract Unification unifyWith(TypeVariable query, Scope scope);

    protected abstract Unification unifyWith(TypeOperator query, Scope scope);

    protected abstract Unification unify_(Type target, Scope scope);
}
