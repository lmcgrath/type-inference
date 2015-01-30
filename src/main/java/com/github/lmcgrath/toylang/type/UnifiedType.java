package com.github.lmcgrath.toylang.type;

import static lombok.AccessLevel.PACKAGE;

import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor(access = PACKAGE)
@EqualsAndHashCode
@ToString
public class UnifiedType extends Unification {

    private final Type type;

    @Override
    public void ifUnified(Consumer<Type> consumer) {
        consumer.accept(type);
    }

    @Override
    public Unification map(Function<Type, Unification> function) {
        return function.apply(type);
    }

    @Override
    public Type orElseGet(Function<Unification, Type> function) {
        return type;
    }

    @Override
    public boolean isUnified() {
        return true;
    }
}
