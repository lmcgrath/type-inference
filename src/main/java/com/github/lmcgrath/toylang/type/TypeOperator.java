package com.github.lmcgrath.toylang.type;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static com.github.lmcgrath.toylang.unification.Unifications.failed;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.github.lmcgrath.toylang.unification.Unification;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TypeOperator implements Type {

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
    public boolean isVariable() {
        return false;
    }

    @Override
    public String toString() {
        if (parameters.size() == 2) {
            return "(" + parameters.get(0) + " " + name + " " + parameters.get(1) + ")";
        } else if (parameters.isEmpty()) {
            return name;
        } else {
            return "(" + name + " " + join(parameters) + ")";
        }
    }

    private String join(List<Type> types) {
        StringBuilder builder = new StringBuilder();
        Iterator<Type> iterator = types.iterator();
        builder.append(iterator.next());
        while (iterator.hasNext()) {
            builder.append(", ");
            builder.append(iterator.next());
        }
        return builder.toString();
    }
}
