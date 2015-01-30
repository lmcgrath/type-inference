package com.github.lmcgrath.toylang.type;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static com.github.lmcgrath.toylang.unification.Unifications.failed;
import static com.github.lmcgrath.toylang.unification.Unifications.mismatch;
import static com.github.lmcgrath.toylang.unification.Unifications.recursive;
import static com.github.lmcgrath.toylang.unification.Unifications.unified;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.unification.Unification;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TypeOperator extends Type {

    public static TypeOperator array(Type type) {
        return new TypeOperator("[]", type);
    }

    public static TypeOperator fn(Type argument, Type result) {
        return new TypeOperator("→", argument, result);
    }

    public static TypeOperator tuple(Type... types) {
        return new TypeOperator("⋅", types);
    }

    public static TypeOperator type(String name) {
        return new TypeOperator(name);
    }

    private final String name;
    private final List<Type> parameters;

    public TypeOperator(String name, Type... parameters) {
        this(name, asList(parameters));
    }

    public TypeOperator(String name, Collection<Type> parameters) {
        this.name = name;
        this.parameters = ImmutableList.copyOf(parameters);
    }

    @Override
    public Unification bind(Type type) {
        return failed(this, type);
    }

    @Override
    public boolean contains(Type type) {
        return parameters.contains(type);
    }

    @Override
    public Type expose() {
        return new TypeOperator(
            name,
            parameters.stream()
                .map(Type::expose)
                .collect(toList()));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Type> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return toString_();
    }

    private boolean is(Type type, String name) {
        return type instanceof TypeOperator && ((TypeOperator) type).name.equals(name);
    }

    @Override
    protected Type genericCopy(Scope scope, Map<Type, Type> mappings) {
        List<Type> copiedParams = parameters.stream()
            .map(parameter -> parameter.genericCopy(scope, mappings))
            .collect(toList());
        return new TypeOperator(name, copiedParams);
    }

    @Override
    protected String toParenthesizedString() {
        if (parameters.isEmpty()) {
            return name;
        } else {
            return "(" + toString_() + ")";
        }
    }

    @Override
    protected String toString_() {
        switch (name) {
            case "⋅":
                return parameters.stream()
                    .map(Type::toParenthesizedString)
                    .collect(joining(" " + name + " "));
            case "→":
                Type left = parameters.get(0);
                Type right = parameters.get(1);
                return (is(right, "→") ? left.toParenthesizedString() : left.toString_()) + " " + name + " " + right;
            case "[]":
                return "[" + parameters.get(0) + "]";
            default:
                if (parameters.isEmpty()) {
                    return name;
                } else {
                    return name + " " + parameters.stream()
                        .map(Type::toParenthesizedString)
                        .collect(joining(" "));
                }
        }
    }

    @Override
    protected Unification unifyWith(TypeOperator query, Scope scope) {
        if (getName().equals(query.getName()) && parameters.size() == query.parameters.size()) {
            List<Type> unifiedParams = new ArrayList<>();
            for (int i = 0; i < parameters.size(); i++) {
                Unification unification = parameters.get(i).unify(query.parameters.get(i), scope);
                unification.ifUnified(unifiedParams::add);
                if (!unification.isUnified()) {
                    return unification;
                }
            }
            return unified(new TypeOperator(getName(), unifiedParams));
        } else {
            return mismatch(this, query);
        }
    }

    @Override
    protected Unification unifyWith(TypeVariable query, Scope scope) {
        if (contains(query)) {
            return recursive(this, query);
        } else {
            return query.bind(this);
        }
    }

    @Override
    protected Unification unify_(Type target, Scope scope) {
        return target.unifyWith(this, scope);
    }
}
