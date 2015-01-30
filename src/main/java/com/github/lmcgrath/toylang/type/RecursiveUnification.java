package com.github.lmcgrath.toylang.type;

import static lombok.AccessLevel.PACKAGE;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor(access = PACKAGE)
@EqualsAndHashCode
@ToString
public class RecursiveUnification extends Unification {

    private final Type type;
    private final Type recurring;
}
