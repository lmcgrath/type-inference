package com.github.lmcgrath.toylang.type;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static com.github.lmcgrath.toylang.type.Unifications.mismatch;
import static com.github.lmcgrath.toylang.type.Unifications.unified;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class SumType extends Type {

    private final String     name;
    private final List<Type> parameters;

    SumType(String name, List<Type> parameters) {
        this.name = name;
        this.parameters = ImmutableList.copyOf(parameters);
    }

    @Override
    public boolean contains(Type type, TypeScope scope) {
        return parameters.stream().anyMatch(parameter -> parameter.contains(type, scope));
    }

    @Override
    public Type expose(TypeScope scope) {
        return Types.sum(name, parameters.stream()
            .map(parameter -> parameter.expose(scope))
            .collect(toList()));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected Type genericCopy(TypeScope scope, Map<Type, Type> mappings) {
        return Types.sum(name, parameters.stream()
            .map(parameter -> parameter.genericCopy(scope, mappings))
            .collect(toList()));
    }

    @Override
    protected String toParenthesizedString() {
        if ("[]".equals(name)) {
            return "[" + parameters.get(0).toString_() + "]";
        } else if (parameters.isEmpty()) {
            return name;
        } else {
            return "(" + name + " " + parameters.stream()
                .map(Type::toString_)
                .collect(joining(" ")) + ")";
        }
    }

    @Override
    protected String toString_() {
        if ("[]".equals(name)) {
            return "[" + parameters.get(0).toString_() + "]";
        } else if (parameters.isEmpty()) {
            return name;
        } else {
            return name + " " + parameters.stream()
                .map(Type::toParenthesizedString)
                .collect(joining(" "));
        }
    }

    @Override
    protected Unification unifyWith(SumType query, TypeScope scope) {
        if (name.equals(query.name) && parameters.size() == query.parameters.size()) {
            List<Type> unifiedParams = new ArrayList<>();
            for (int i = 0; i < parameters.size(); i++) {
                Unification unification = parameters.get(i).unify(query.parameters.get(i), scope);
                unification.ifUnified(unifiedParams::add);
                if (!unification.isUnified()) {
                    return unification;
                }
            }
            return unified(Types.sum(name, unifiedParams));
        } else {
            return mismatch(this, query);
        }
    }

    @Override
    protected Unification unify_(Type target, TypeScope scope) {
        return target.unifyWith(this, scope);
    }
}
