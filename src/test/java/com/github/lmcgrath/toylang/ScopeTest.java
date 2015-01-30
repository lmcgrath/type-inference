package com.github.lmcgrath.toylang;

import static com.github.lmcgrath.toylang.type.ProductType.product;
import static com.github.lmcgrath.toylang.type.Types.fn;
import static com.github.lmcgrath.toylang.type.Types.type;
import static com.github.lmcgrath.toylang.type.Types.var;
import static com.github.lmcgrath.toylang.type.Unifications.mismatch;
import static com.github.lmcgrath.toylang.type.Unifications.recursive;
import static com.github.lmcgrath.toylang.type.Unifications.unified;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.github.lmcgrath.toylang.type.Type;
import com.github.lmcgrath.toylang.type.Unification;
import org.junit.Before;
import org.junit.Test;

public class ScopeTest {

    protected Scope scope;

    @Test
    public void functionShouldNotOccurWithinTuple() {
        assertThat(product(var("a"), var("b")).contains(fn(var("a"), var("b")), scope), is(false));
    }

    @Before
    public void setUp() {
        scope = new Scope();
    }

    @Test
    public void shouldNotUnifyConcreteTypes_whenTheirNamesDontMatch() {
        Type left = fn(type("int"), type("bool"));
        Type right = product(type("int"), type("bool"));
        unify(left, right).shouldMismatch(right, left);
    }

    @Test
    public void shouldNotUnifyConcreteTypes_whenTheirSubtypesDontMatch() {
        Type left = fn(type("int"), type("bool"));
        Type right = fn(type("int"), type("int"));
        unify(left, right).shouldMismatch(type("bool"), type("int"));
    }

    @Test
    public void shouldNotUnifyGenericType_whenConcreteTypeContainsGenericType() {
        Type left = var("a");
        Type right = fn(var("b"), var("a"));
        unify(left, right).shouldRecurse(right, left);
    }

    @Test
    public void shouldUnifyGenericTypeToConcreteType() {
        Type left = var("a");
        Type right = fn(var("b"), var("c"));
        unify(left, right).shouldGive(right);
    }

    @Test
    public void shouldUnifyGenericType_whenOtherTypeIsDifferent() {
        Type left = var("a");
        Type right = var("b");
        unify(left, right).shouldGive(var("a"));
    }

    @Test
    public void shouldUnifySimilarGenericTypes() {
        Type left = var("a");
        Type right = var("a");
        unify(left, right).shouldGive(right);
    }

    @Test
    public void shouldUnifyToLeft_whenRightTypeIsGeneric() {
        Type left = fn(var("b"), var("c"));
        Type right = var("a");
        unify(left, right).shouldGive(left);
    }

    @Test
    public void variableShouldNotOccurWithinDifferentVariable() {
        assertThat(var("a").contains(var("b"), scope), is(false));
    }

    @Test
    public void variableShouldOccurWithinFunction() {
        assertThat(fn(var("a"), var("b")).contains(var("a"), scope), is(true));
    }

    @Test
    public void variableShouldOccurWithinSimilarVariable() {
        assertThat(var("a").contains(var("a"), scope), is(true));
    }

    protected UnificationMatcher unify(Type left, Type right) {
        return new UnificationMatcher(left.unify(right, scope));
    }

    protected final class UnificationMatcher {

        private final Unification result;

        public UnificationMatcher(Unification result) {
            this.result = result;
        }

        public void shouldBe(Unification unification) {
            assertThat(result, is(unification));
        }

        public void shouldGive(Type type) {
            shouldBe(unified(type));
        }

        public void shouldMismatch(Type expected, Type actual) {
            shouldBe(mismatch(expected, actual));
        }

        public void shouldRecurse(Type expected, Type recurring) {
            shouldBe(recursive(expected, recurring));
        }
    }
}
