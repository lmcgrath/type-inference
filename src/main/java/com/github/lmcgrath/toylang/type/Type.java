package com.github.lmcgrath.toylang.type;

import static com.github.lmcgrath.toylang.type.Unifications.failed;
import static com.github.lmcgrath.toylang.type.Unifications.mismatch;
import static com.github.lmcgrath.toylang.type.Unifications.recursive;

import java.util.HashMap;
import java.util.Map;

public abstract class Type {

    public Unification bind(Type type, TypeScope scope) {
        return failed(this, type);
    }

    public abstract boolean contains(Type type, TypeScope scope);

    @Override
    public abstract boolean equals(Object o);

    public abstract Type expose(TypeScope scope);

    public Type genericCopy(TypeScope scope) {
        return expose(scope).genericCopy(scope, new HashMap<>());
    }

    public abstract String getName();

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

    protected Unification unifyVariable(Type type, TypeScope scope) {
        if (type.contains(this, scope)) {
            return recursive(type, this);
        } else {
            return bind(type, scope);
        }
    }

    protected Unification unifyWith(ProductType query, TypeScope scope) {
        return mismatch(this, query);
    }

    protected Unification unifyWith(SumType query, TypeScope scope) {
        return mismatch(this, query);
    }

    protected Unification unifyWith(VariableType query, TypeScope scope) {
        return query.unifyVariable(this, scope);
    }

    protected Unification unifyWith(FunctionType query, TypeScope scope) {
        return mismatch(this, query);
    }

    protected abstract Unification unify_(Type target, TypeScope scope);
}
