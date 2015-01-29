package com.github.lmcgrath.toylang;

import static com.github.lmcgrath.toylang.TypeOperator.func;

public class TypeChecker implements ExpressionVisitor<Type, TypeEnvironment> {

    public Type analyze(Expression expression, TypeEnvironment environment) throws TypeException {
        return expression.accept(this, environment).expose();
    }

    @Override
    public Type visitApply(Apply apply, TypeEnvironment environment) throws TypeException {
        Type functionType = analyze(apply.getFunction(), environment);
        Type argumentType = analyze(apply.getArgument(), environment);
        Type resultType = environment.createVariable();
        environment.unify(func(argumentType, resultType), functionType);
        return resultType;
    }

    @Override
    public Type visitLet(Let let, TypeEnvironment environment) throws TypeException {
        Type defType = analyze(let.getDefinition(), environment);
        TypeEnvironment letEnv = environment.extend();
        letEnv.define(let.getName(), defType);
        return analyze(let.getScope(), letEnv);
    }

    @Override
    public Type visitLetRecursive(LetRecursive let, TypeEnvironment environment) throws TypeException {
        Type defVarType = environment.createVariable();
        TypeEnvironment letEnv = environment.extend();
        letEnv.define(let.getName(), defVarType);
        letEnv.specialize(defVarType);
        Type defActualType = analyze(let.getDefinition(), letEnv);
        environment.unify(defVarType, defActualType);
        letEnv.generify(defVarType);
        return analyze(let.getScope(), letEnv);
    }

    @Override
    public Type visitIdentifier(Identifier identifier, TypeEnvironment environment) throws TypeException {
        return environment.typeOf(identifier.getName());
    }

    @Override
    public Type visitLambda(Lambda lambda, TypeEnvironment environment) throws TypeException {
        TypeEnvironment lambdaEnv = environment.extend();
        Type argType = environment.createVariable();
        lambdaEnv.define(lambda.getVariable(), argType);
        lambdaEnv.specialize(argType);
        return func(argType, analyze(lambda.getBody(), lambdaEnv));
    }
}
