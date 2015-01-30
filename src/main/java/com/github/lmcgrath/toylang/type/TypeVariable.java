package com.github.lmcgrath.toylang.type;

import static com.github.lmcgrath.toylang.type.Unifications.recursive;
import static com.github.lmcgrath.toylang.type.Unifications.unified;

import java.util.List;
import java.util.Map;
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
    public List<Type> getParameters() {
        return ImmutableList.of();
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
    protected Unification unifyWith(TypeVariable query, TypeScope scope) {
        if (equals(query)) {
            return unified(this);
        } else {
            return bind(query, scope);
        }
    }

    @Override
    protected Unification unifyWith(TypeOperator query, TypeScope scope) {
        if (query.contains(this, scope)) {
            return recursive(this, query);
        } else {
            return bind(query, scope);
        }
    }

    @Override
    protected Unification unify_(Type target, TypeScope scope) {
        return target.unifyWith(this, scope);
    }
}
