package com.github.lmcgrath.toylang;

import static java.util.Arrays.asList;

import java.util.*;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.EqualsBuilder;

public class TypeOperator implements Type {

    public static TypeOperator array(Type type) {
        return new TypeOperator("[]", type);
    }

    public static TypeOperator func(Type argument, Type result) {
        return new TypeOperator("->", argument, result);
    }

    public static TypeOperator tuple(Type... types) {
        return new TypeOperator("x", types);
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
    public void bind(Type type) {
        throw new IllegalStateException("Cannot bind to non-generic type");
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof TypeOperator) {
            TypeOperator other = (TypeOperator) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(parameters, other.parameters)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public Type expose() {
        List<Type> types = new ArrayList<>();
        for (Type type : getParameters()) {
            types.add(type.expose());
        }
        return new TypeOperator(name, types);
    }

    public TypeOperator extend(Type type) {
        return new TypeOperator(name, ImmutableList.<Type>builder().addAll(parameters).add(type).build());
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
    public int hashCode() {
        return Objects.hash(name, parameters);
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
