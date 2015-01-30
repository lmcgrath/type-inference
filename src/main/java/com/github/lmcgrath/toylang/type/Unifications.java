package com.github.lmcgrath.toylang.type;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class Unifications {

    public static Unification unified(Type type) {
        return new UnifiedType(type);
    }

    public static Unification mismatch(Type expected, Type actual) {
        return new TypeMismatch(expected, actual);
    }

    public static Unification failed(Type expected, Type actual) {
        return new FailedBinding(expected, actual);
    }

    public static Unification undefined(String name) {
        return new UndefinedSymbol(name);
    }

    public static Unification recursive(Type expected, Type recurring) {
        return new RecursiveUnification(expected, recurring);
    }
}
