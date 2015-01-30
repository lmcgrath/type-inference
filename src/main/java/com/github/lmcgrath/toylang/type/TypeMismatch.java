package com.github.lmcgrath.toylang.type;

import static lombok.AccessLevel.PACKAGE;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor(access = PACKAGE)
@EqualsAndHashCode
@ToString
public class TypeMismatch extends Unification {

    private final Type expected;
    private final Type actual;
}
