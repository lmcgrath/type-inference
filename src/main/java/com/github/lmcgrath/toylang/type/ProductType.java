package com.github.lmcgrath.toylang.type;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ProductType extends Type {

    public static Type product(Type... members) {
        return product(asList(members));
    }

    public static Type product(List<Type> members) {
        return new ProductType(members);
    }

    private final List<Type> members;

    public ProductType(List<Type> members) {
        this.members = ImmutableList.copyOf(members);
    }

    @Override
    public boolean contains(Type type, TypeScope scope) {
        return members.stream().anyMatch(member -> member.contains(type, scope));
    }

    @Override
    public Type expose(TypeScope scope) {
        return product(members.stream()
            .map(member -> member.expose(scope))
            .collect(toList()));
    }

    @Override
    public String getName() {
        return "⋅";
    }

    @Override
    protected Type genericCopy(TypeScope scope, Map<Type, Type> mappings) {
        return product(members.stream()
            .map(member -> member.genericCopy(scope, mappings))
            .collect(toList()));
    }

    @Override
    protected String toParenthesizedString() {
        return toString_();
    }

    @Override
    protected String toString_() {
        return "(" + members.stream()
            .map(Type::toString_)
            .collect(joining(" " + getName() + " ")) + ")";
    }

    @Override
    protected Unification unify_(Type target, TypeScope scope) {
        return target.unifyWith(this, scope);
    }
}
