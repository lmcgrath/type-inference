package com.github.lmcgrath.toylang;

import static com.github.lmcgrath.toylang.TypeVariable.var;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class TypeVariableTest {

    private TypeVariable variable;
    private TypeVariable similarVariable;
    private TypeVariable variableWithDifferentName;
    private TypeVariable boundVariable;
    private TypeVariable similarBoundVariable;
    private TypeVariable differentlyBoundVariable;

    @Before
    public void setUp() {
        variable = var("a");
        similarVariable = var("a");
        variableWithDifferentName = var("b");
        Type type1 = mock(Type.class);
        Type type2 = mock(Type.class);
        boundVariable = var("a");
        similarBoundVariable = var("a");
        differentlyBoundVariable = var("a");
        boundVariable.bind(type1);
        similarBoundVariable.bind(type1);
        differentlyBoundVariable.bind(type2);
    }

    @Test
    public void shouldBeEqual_whenOtherVariableIsItself() {
        assertThat(variable.equals(variable), is(true));
        assertThat(variable.hashCode(), equalTo(variable.hashCode()));
    }

    @Test
    public void shouldBeEqual_whenOtherVariableIsSimilar() {
        assertThat(variable.equals(similarVariable), is(true));
        assertThat(variable.hashCode(), equalTo(similarVariable.hashCode()));
    }

    @Test
    public void shouldNotBeEqual_whenOtherVariableHasDifferentName() {
        assertThat(variable.equals(variableWithDifferentName), is(false));
        assertThat(variable.hashCode(), not(equalTo(variableWithDifferentName.hashCode())));
    }

    @Test
    public void shouldNotBeEqual_whenOtherVariableIsBound() {
        assertThat(variable.equals(boundVariable), is(false));
        assertThat(variable.hashCode(), not(equalTo(boundVariable.hashCode())));
    }

    @Test
    public void shouldBeEqual_whenOtherBoundVariableIsSimilar() {
        assertThat(boundVariable.equals(similarBoundVariable), is(true));
        assertThat(boundVariable.hashCode(), equalTo(similarBoundVariable.hashCode()));
    }

    @Test
    public void shouldNotBeEqual_whenOtherVariableIsBoundDifferently() {
        assertThat(boundVariable.equals(differentlyBoundVariable), is(false));
        assertThat(boundVariable.hashCode(), not(equalTo(differentlyBoundVariable.hashCode())));
    }
}
