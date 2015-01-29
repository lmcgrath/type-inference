package com.github.lmcgrath.toylang;

import static com.github.lmcgrath.toylang.TypeOperator.func;
import static com.github.lmcgrath.toylang.TypeOperator.tuple;
import static com.github.lmcgrath.toylang.TypeOperator.type;
import static com.github.lmcgrath.toylang.TypeVariable.var;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TypeEnvironmentTest {

    private TypeEnvironment environment;

    @Before
    public void setUp() {
        environment = new TypeEnvironment();
    }

    @Test
    public void functionShouldNotOccurWithinTuple() {
        assertThat(environment.occursIn(func(var("a"), var("b")), tuple(var("a"), var("b"))), is(false));
    }

    @Test
    public void variableShouldNotOccurWithinDifferentVariable() {
        assertThat(environment.occursIn(var("a"), var("b")), is(false));
    }

    @Test
    public void variableShouldOccurWithinFunction() {
        assertThat(environment.occursIn(var("a"), func(var("a"), var("b"))), is(true));
    }

    @Test
    public void variableShouldOccurWithinSimilarVariable() {
        assertThat(environment.occursIn(var("a"), var("a")), is(true));
    }

    @Test(expected = TypeException.class)
    public void shouldNotUnifyConcreteTypes_whenTheirNamesDontMatch() {
        Type left = func(type("int"), type("bool"));
        Type right = tuple(type("int"), type("bool"));
        environment.unify(left, right);
    }

    @Test(expected = TypeException.class)
    public void shouldNotUnifyConcreteTypes_whenTheirSubtypesDontMatch() {
        Type left = func(type("int"), type("bool"));
        Type right = func(type("int"), type("int"));
        environment.unify(left, right);
    }

    @Test(expected = TypeException.class)
    public void shouldNotUnifyGenericType_whenConcreteTypeContainsGenericType() {
        Type left = var("a");
        Type right = func(var("b"), var("a"));
        environment.unify(left, right);
    }

    @Test
    public void shouldUnifyGenericTypeToConcreteType() {
        Type left = var("a");
        Type right = func(var("b"), var("c"));
        environment.unify(left, right);
        assertThat(left.expose(), equalTo(right));
    }

    @Test
    public void shouldUnifyGenericType_whenOtherTypeIsDifferent() {
        Type left = var("a");
        Type right = var("b");
        environment.unify(left, right);
        assertThat(left.expose(), equalTo(right));
    }

    @Test
    public void shouldUnifySimilarGenericTypes() {
        Type left = var("a");
        Type right = var("a");
        environment.unify(left, right);
    }

    @Test
    public void shouldUnifyToLeft_whenRightTypeIsGeneric() {
        Type left = func(var("b"), var("c"));
        Type right = var("a");
        environment.unify(left, right);
        assertThat(right.expose(), equalTo(left));
    }
}
