package com.github.lmcgrath.toylang.type;

import java.util.List;
import com.github.lmcgrath.toylang.unification.Unification;

public interface Type {

    Unification bind(Type type);

    Type expose();

    String getName();

    List<Type> getParameters();

    boolean isVariable();
}
