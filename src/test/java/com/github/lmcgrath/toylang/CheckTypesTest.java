package com.github.lmcgrath.toylang;

import static com.github.lmcgrath.toylang.type.TypeOperator.fn;
import static com.github.lmcgrath.toylang.type.TypeOperator.tuple;
import static com.github.lmcgrath.toylang.type.TypeOperator.type;
import static com.github.lmcgrath.toylang.type.TypeVariable.var;
import static com.github.lmcgrath.toylang.type.Unifications.mismatch;
import static com.github.lmcgrath.toylang.type.Unifications.recursive;
import static com.github.lmcgrath.toylang.type.Unifications.undefined;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import com.github.lmcgrath.toylang.expression.Expression;
import com.github.lmcgrath.toylang.type.Type;
import com.github.lmcgrath.toylang.type.Unification;
import org.junit.Test;

public class CheckTypesTest {

    private ExpressionBuilder builder;

    @Test
    public void identityLambdaShouldHaveGenericType() {
        given(new ExpressionBuilder() {{
            lambda("x", id("x"));
        }});
        checkTypes().shouldGive(fn(var("f"), var("f")));
    }

    @Test
    public void shouldNotUnifySelfApplication() {
        given(new ExpressionBuilder() {{
            lambda("x", apply(id("x"), id("x")));
        }});
        checkTypes().shouldHaveRecursive(fn(var("h"), var("i")), var("h"));
    }

    @Test
    public void factorialShouldHaveTypeOfIntToInt() {
        given(new ExpressionBuilder() {{
            letrec(
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
        }});
        checkTypes().shouldGive(fn(type("Int"), type("Int")));
    }

    @Test
    public void factorialOfFiveShouldHaveTypeOfInt() {
        given(new ExpressionBuilder() {{
            letrec(
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
        }});
        checkTypes().shouldGive(type("Int"));
    }

    @Test
    public void fibonacciShouldHaveTypeOfIntToInt() {
        given(new ExpressionBuilder() {{
            letrec(
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
        }});
        checkTypes().shouldGive(fn(type("Int"), type("Int")));
    }

    @Test
    public void fibonacciOfTrueShouldBeTypeMismatch() {
        given(new ExpressionBuilder() {{
            letrec(
                "fibonacci",
                lambda("n", apply(
                    apply(apply(id("cond"), apply(apply(id("<="), id("n")), id("1"))), id("n")),
                    apply(
                        apply(id("+"), apply(id("fibonacci"), apply(apply(id("-"), id("n")), id("1")))),
                        apply(id("fibonacci"), apply(apply(id("-"), id("n")), id("2")))
                    )
                )),
                apply(id("fibonacci"), id("True"))
            );
        }});
        checkTypes().shouldHaveMismatch(type("Int"), type("Bool"));
    }

    @Test
    public void shouldBeTypeMismatch_whenGenericTypesDoNotAlign() {
        given(new ExpressionBuilder() {{
            lambda("x", apply(
                apply(id("pair"), apply(id("x"), id("3"))),
                apply(id("x"), id("True"))
            ));
        }});
        checkTypes().shouldHaveError(mismatch(type("Int"), type("Bool")));
    }

    @Test
    public void shouldReportUndefinedSymbol() {
        given(new ExpressionBuilder() {{
            apply(apply(id("pair"), apply(id("f"), id("4"))), apply(id("f"), id("True")));
        }});
        checkTypes().shouldNotBeDefined("f");
    }

    @Test
    public void pairShouldHaveIntAndBoolTypes() {
        given(new ExpressionBuilder() {{
            apply(apply(id("pair"), id("3")), id("True"));
        }});
        checkTypes().shouldGive(tuple(type("Int"), type("Bool")));
    }

    @Test
    public void pairShouldHaveIntAndBoolTypes_whenUsingIdentityFunction() {
        given(new ExpressionBuilder() {{
            let("f", lambda("x", id("x")),
                apply(apply(id("pair"), apply(id("f"), id("3"))), apply(id("f"), id("True"))));
        }});
        checkTypes().shouldGive(tuple(type("Int"), type("Bool")));
    }

    @Test
    public void functionReturningIntEachTimeShouldHaveTypeOfInt() {
        given(new ExpressionBuilder() {{
            let("g", lambda("f", id("5")),
                apply(id("g"), id("g"))
            );
        }});
        checkTypes().shouldGive(type("Int"));
    }

    @Test
    public void genericFunctionCompositionShouldHaveGenericType() {
        given(new ExpressionBuilder() {{
            lambda("f", lambda("g", lambda("arg",
                apply(id("g"), apply(id("f"), id("arg")))
            )));
        }});
        checkTypes().shouldGive(fn(
            fn(var("n"), var("o")),
            fn(
                fn(var("o"), var("p")),
                fn(var("n"), var("p"))
            )));
    }

    private UnificationMatcher checkTypes() {
        return new UnificationMatcher();
    }

    private void given(ExpressionBuilder builder) {
        Type BoolType = type("Bool");
        Type IntType = type("Int");
        Type a = builder.reserveType();
        Type b = builder.reserveType();
        Type c = builder.reserveType();
        Type pairType = tuple(a, b);
        this.builder = builder
            .define("Bool", BoolType)
            .define("Int", IntType)
            .define("1", IntType)
            .define("2", IntType)
            .define("3", IntType)
            .define("4", IntType)
            .define("5", IntType)
            .define("pair", fn(a, fn(b, pairType)))
            .define("True", BoolType)
            .define("zero?", fn(IntType, BoolType))
            .define("pred", fn(IntType, IntType))
            .define("cond", fn(BoolType, fn(c, fn(c, c))))
            .define("<=", fn(IntType, fn(IntType, BoolType)))
            .define("*", fn(IntType, fn(IntType, IntType)))
            .define("+", fn(IntType, fn(IntType, IntType)))
            .define("-", fn(IntType, fn(IntType, IntType)));
    }

    private final class UnificationMatcher {

        public void shouldGive(Type type) {
            Expression expression = builder.checkTypes();
            assertThat(builder.getErrors(), is(empty()));
            assertThat(expression.getType().expose(builder.getScope()), is(type)); // TODO expose? really?
        }

        public void shouldHaveError(Unification unification) {
            builder.checkTypes();
            assertThat(builder.getErrors(), is(not(empty())));
            assertThat(builder.getErrors(), contains(unification));
        }

        public void shouldHaveMismatch(Type expected, Type actual) {
            shouldHaveError(mismatch(expected, actual));
        }

        public void shouldHaveRecursive(Type expected, Type recurring) {
            shouldHaveError(recursive(expected, recurring));
        }

        public void shouldNotBeDefined(String name) {
            shouldHaveError(undefined(name));
        }
    }
}
