package com.github.lmcgrath.toylang.unification;

import java.util.function.Consumer;
import java.util.function.Function;
import com.github.lmcgrath.toylang.type.Type;

public abstract class Unification {

    public void ifUnified(Consumer<Type> consumer) {
        // no-op
    }

    public Unification map(Function<Type, Unification> function) {
        return this;
    }

    public Type orElseGet(Function<Unification, Type> function) {
        return function.apply(this);
    }

    public boolean isUnified() {
        return false;
    }
}
