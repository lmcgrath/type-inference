package com.github.lmcgrath.toylang.type;

import static com.github.lmcgrath.toylang.unification.Unifications.recursive;
import static com.github.lmcgrath.toylang.unification.Unifications.unified;

import java.util.List;
import java.util.Map;
import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.unification.Unification;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TypeVariable extends Type {

    public static TypeVariable var(String name) {
        return new TypeVariable(name);
    }

    private final String name;

    public TypeVariable(String name) {
        this.name = name;
    }

    @Override
    public Unification bind(Type type, Scope scope) {
        return scope.bind(this, type);
    }

    @Override
    public boolean contains(Type type, Scope scope) {
        return equals(type.expose(scope));
    }

    @Override
    public Type expose(Scope scope) {
        return scope.expose(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Type> getParameters() {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        return toString_();
    }

    @Override
    protected Type genericCopy(Scope scope, Map<Type, Type> mappings) {
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
    protected Unification unifyWith(TypeVariable query, Scope scope) {
        if (equals(query)) {
            return unified(this);
        } else {
            return bind(query, scope);
        }
    }

    @Override
    protected Unification unifyWith(TypeOperator query, Scope scope) {
        if (query.contains(this, scope)) {
            return recursive(this, query);
        } else {
            return bind(query, scope);
        }
    }

    @Override
    protected Unification unify_(Type target, Scope scope) {
        return target.unifyWith(this, scope);
    }
}
