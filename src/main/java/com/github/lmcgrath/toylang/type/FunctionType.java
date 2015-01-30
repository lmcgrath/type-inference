package com.github.lmcgrath.toylang.type;

import static com.github.lmcgrath.toylang.type.Types.fn;
import static com.github.lmcgrath.toylang.type.Unifications.unified;

import java.util.Map;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class FunctionType extends Type {

    private final Type argument;
    private final Type result;

    FunctionType(Type argument, Type result) {
        this.argument = argument;
        this.result = result;
    }

    @Override
    public boolean contains(Type type, TypeScope scope) {
        return argument.contains(type, scope) || result.contains(type, scope);
    }

    @Override
    public Type expose(TypeScope scope) {
        return fn(argument.expose(scope), result.expose(scope));
    }

    @Override
    public String getName() {
        return "→";
    }

    @Override
    public String toString() {
        return toString_();
    }

    @Override
    protected Type genericCopy(TypeScope scope, Map<Type, Type> mappings) {
        return fn(argument.genericCopy(scope, mappings), result.genericCopy(scope, mappings));
    }

    @Override
    protected String toParenthesizedString() {
        return "(" + toString_() + ")";
    }

    @Override
    protected String toString_() {
        String argumentString = argument instanceof FunctionType
            ? argument.toParenthesizedString()
            : argument.toString_();
        return argumentString + " " + getName() + " " + result.toString_();
    }

    @Override
    protected Unification unifyWith(FunctionType query, TypeScope scope) {
        return argument.unify_(query.argument, scope)
            .map(unifiedArgument -> result.unify_(query.result, scope)
                .map(unifiedResult -> unified(fn(unifiedArgument, unifiedResult))));
    }

    @Override
    protected Unification unify_(Type target, TypeScope scope) {
        return target.unifyWith(this, scope);
    }
}
