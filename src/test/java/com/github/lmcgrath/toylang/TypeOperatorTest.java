package com.github.lmcgrath.toylang;

import static com.github.lmcgrath.toylang.TypeOperator.func;
import static com.github.lmcgrath.toylang.TypeOperator.tuple;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class TypeOperatorTest {

    private TypeOperator operator;
    private TypeOperator similarOperator;
    private TypeOperator operatorWithDifferentName;
    private TypeOperator operatorWithDifferentTypes;

    @Before
    public void setUp() {
        Type type1 = mock(Type.class);
        Type type2 = mock(Type.class);
        operator = func(type1, type2);
        similarOperator = func(type1, type2);
        operatorWithDifferentName = tuple(type1, type2);
        operatorWithDifferentTypes = func(type1, mock(Type.class));
    }

    @Test
    public void shouldBeEqual_whenOtherOperatorIsItself() {
        assertThat(operator.equals(operator), is(true));
        assertThat(operator.hashCode(), equalTo(operator.hashCode()));
    }

    @Test
    public void shouldBeEqual_whenOtherOperatorIsSimilar() {
        assertThat(operator.equals(similarOperator), is(true));
        assertThat(operator.hashCode(), equalTo(similarOperator.hashCode()));
    }

    @Test
    public void shouldNotBeEqual_whenOtherOperatorHasDifferentName() {
        assertThat(operator.equals(operatorWithDifferentName), is(false));
        assertThat(operator.hashCode(), not(equalTo(operatorWithDifferentName.hashCode())));
    }

    @Test
    public void shouldNotBeEqual_whenOtherOperatorHasDifferentTypes() {
        assertThat(operator.equals(operatorWithDifferentTypes), is(false));
        assertThat(operator.hashCode(), not(equalTo(operatorWithDifferentTypes.hashCode())));
    }
}
