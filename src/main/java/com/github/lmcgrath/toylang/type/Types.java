package com.github.lmcgrath.toylang.type;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import com.google.common.collect.ImmutableList;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class Types {

    public static Type fn(Type argument, Type result) {
        return new FunctionType(argument, result);
    }

    public static Type list(Type parameter) {
        return sum("[]", ImmutableList.of(parameter));
    }

    public static Type type(String name) {
        return sum(name, ImmutableList.of());
    }

    public static VariableType var(String name) {
        return new VariableType(name);
    }

    public static Type sum(String name, List<Type> parameters) {
        return new SumType(name, parameters);
    }
}
