package com.github.lmcgrath.toylang;

import static com.github.lmcgrath.toylang.ExpressionFactory.*;
import static com.github.lmcgrath.toylang.TypeOperator.func;
import static com.github.lmcgrath.toylang.TypeOperator.tuple;
import static com.github.lmcgrath.toylang.TypeOperator.type;
import static com.github.lmcgrath.toylang.TypeVariable.var;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TypeCheckerTest {

    private TypeChecker     analyzer;
    private TypeEnvironment environment;

    @Before
    public void setUp() {
        analyzer = new TypeChecker();
        environment = createEnvironment();
    }

    @Test
    public void identityLambdaShouldHaveGenericType() {
        Expression identity = lambda("x", id("x"));
        Type expectedType = func(var("d"), var("d"));
        assertThat(analyzer.analyze(identity, environment), equalTo(expectedType));
    }

    @Test(expected = TypeException.class)
    public void shouldNotUnifySelfApplication() {
        Expression selfApplication = lambda("x", apply(id("x"), id("x")));
        analyzer.analyze(selfApplication, environment);
    }

    @Test
    public void factorialShouldHaveTypeOfIntToInt() {
        Expression expression = letrec(
            "factorial",
            lambda("n", apply(
                apply(apply(id("cond"), apply(id("zero?"), id("n"))), id("1")),
                apply(
                    apply(id("*"), id("n")),
                    apply(id("factorial"), apply(id("pred"), id("n")))
                )
            )),
            id("factorial")
        );
        Type expectedType = func(type("int"), type("int"));
        assertThat(analyzer.analyze(expression, environment), equalTo(expectedType));
    }

    @Test
    public void factorialOfFiveShouldHaveTypeOfInt() {
        Expression expression = letrec(
            "factorial",
            lambda("n", apply(
                apply(apply(id("cond"), apply(id("zero?"), id("n"))), id("1")),
                apply(
                    apply(id("*"), id("n")),
                    apply(id("factorial"), apply(id("pred"), id("n")))
                )
            )),
            apply(id("factorial"), id("5"))
        );
        assertThat(analyzer.analyze(expression, environment), equalTo(environment.typeOf("int")));
    }

    @Test
    public void fibonacciShouldHaveTypeOfIntToInt() {
        Expression expression = letrec(
            "fibonacci",
            lambda("n", apply(
                apply(apply(id("cond"), apply(apply(id("<="), id("n")), id("1"))), id("n")),
                apply(
                    apply(id("+"), apply(id("fibonacci"), apply(apply(id("-"), id("n")), id("1")))),
                    apply(id("fibonacci"), apply(apply(id("-"), id("n")), id("2")))
                )
            )),
            id("fibonacci")
        );
        Type expectedType = func(type("int"), type("int"));
        assertThat(analyzer.analyze(expression, environment), equalTo(expectedType));
    }

    @Test(expected = TypeException.class)
    public void fibonacciOfTrueShouldBeTypeMismatch() {
        Expression expression = letrec(
            "fibonacci",
            lambda("n", apply(
                apply(apply(id("cond"), apply(apply(id("<="), id("n")), id("1"))), id("n")),
                apply(
                    apply(id("+"), apply(id("fibonacci"), apply(apply(id("-"), id("n")), id("1")))),
                    apply(id("fibonacci"), apply(apply(id("-"), id("n")), id("2")))
                )
            )),
            apply(id("fibonacci"), id("true"))
        );
        analyzer.analyze(expression, environment);
    }

    @Test(expected = TypeException.class)
    public void shouldBeTypeMismatch_whenGenericTypesAreNotKnownToAlign() {
        Expression expression = lambda("x", apply(
            apply(id("pair"), apply(id("x"), id("3"))),
            apply(id("x"), id("true"))
        ));
        analyzer.analyze(expression, environment);
    }

    @Test(expected = UndefinedSymbolException.class)
    public void shouldReportUndefinedSymbol() {
        Expression expression = apply(apply(id("pair"), apply(id("f"), id("4"))), apply(id("f"), id("true")));
        Type expectedType = tuple(type("int"), type("bool"));
        assertThat(analyzer.analyze(expression, environment), equalTo(expectedType));
    }

    @Test
    public void pairShouldHaveIntAndBoolTypes() {
        Expression expression = apply(apply(id("pair"), id("3")), id("true"));
        Type expectedType = tuple(type("int"), type("bool"));
        assertThat(analyzer.analyze(expression, environment), equalTo(expectedType));
    }

    @Test
    public void pairShouldHaveIntAndBoolTypes_whenUsingIdentityFunction() {
        Expression expression = let(
            "f",
            lambda("x", id("x")),
            apply(apply(id("pair"), apply(id("f"), id("3"))), apply(id("f"), id("true")))
        );
        Type expectedType = tuple(type("int"), type("bool"));
        assertThat(analyzer.analyze(expression, environment), equalTo(expectedType));
    }

    @Test
    public void functionReturningIntEachTimeShouldHaveTypeOfInt() {
        Expression expression = let(
            "g",
            lambda("f", id("5")),
            apply(id("g"), id("g"))
        );
        Type expectedType = type("int");
        assertThat(analyzer.analyze(expression, environment), equalTo(expectedType));
    }

    @Test
    public void shouldHandleGenericAndNonGenericTypes() {
        Expression expression = lambda("g", let("f", lambda("x", id("g")),
            apply(apply(id("pair"), apply(id("f"), id("3"))), apply(id("f"), id("true")))
        ));
        Type expectedType = func(var("k"), tuple(var("k"), var("k")));
        assertThat(analyzer.analyze(expression, environment), equalTo(expectedType));
    }

    @Test
    public void genericFunctionCompositionShouldHaveGenericType() {
        Expression expression = lambda("f", lambda("g", lambda("arg",
            apply(id("g"), apply(id("f"), id("arg")))
        )));
        Type expectedType = func(func(var("f"), var("g")), func(
            func(var("g"), var("h")),
            func(var("f"), var("h"))
        ));
        assertThat(analyzer.analyze(expression, environment), equalTo(expectedType));
    }

    private TypeEnvironment createEnvironment() {
        TypeEnvironment symbols = new TypeEnvironment();
        Type boolType = type("bool");
        Type intType = type("int");
        Type var1 = symbols.createVariable();
        Type var2 = symbols.createVariable();
        Type var3 = symbols.createVariable();
        Type pairType = tuple(var1, var2);
        symbols.define("bool", boolType);
        symbols.define("int", intType);
        symbols.define("1", intType);
        symbols.define("2", intType);
        symbols.define("3", intType);
        symbols.define("4", intType);
        symbols.define("5", intType);
        symbols.define("pair", func(var1, func(var2, pairType)));
        symbols.define("true", boolType);
        symbols.define("zero?", func(intType, boolType));
        symbols.define("pred", func(intType, intType));
        symbols.define("cond", func(boolType, func(var3, func(var3, var3))));
        symbols.define("<=", func(intType, func(intType, boolType)));
        symbols.define("*", func(intType, func(intType, intType)));
        symbols.define("+", func(intType, func(intType, intType)));
        symbols.define("-", func(intType, func(intType, intType)));
        return symbols;
    }
}
