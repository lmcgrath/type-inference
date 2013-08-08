package com.github.lmcgrath.toylang;

import java.util.List;

public interface Type {

    void bind(Type type);

    Type expose();

    String getName();

    List<Type> getParameters();

    boolean isVariable();
}
