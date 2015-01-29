package com.github.lmcgrath.toylang.unification;

import static lombok.AccessLevel.PACKAGE;

import com.github.lmcgrath.toylang.type.Type;
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
