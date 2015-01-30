package com.github.lmcgrath.toylang.type;

import static java.util.Collections.emptyList;
import static com.github.lmcgrath.toylang.unification.Unifications.failed;
import static com.github.lmcgrath.toylang.unification.Unifications.recursive;
import static com.github.lmcgrath.toylang.unification.Unifications.unified;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.github.lmcgrath.toylang.Scope;
import com.github.lmcgrath.toylang.unification.Unification;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TypeVariable extends Type {

    public static TypeVariable var(String name) {
        return new TypeVariable(name);
    }

    private State state;

    public TypeVariable(String name) {
        this.state = new UnboundState(this, name);
    }

    @Override
    public Unification bind(Type type) {
        return state.bind(type);
    }

    @Override
    public boolean contains(Type type) {
        return state.contains(type.expose());
    }

    @Override
    public Type expose() {
        return state.expose();
    }

    @Override
    public String getName() {
        return state.getName();
    }

    @Override
    public List<Type> getParameters() {
        return state.getParameters();
    }

    @Override
    public String toString() {
        return state.toString();
    }

    @Override
    protected Type genericCopy(Scope scope, Map<Type, Type> mappings) {
        if (scope.isGeneric(this)) {
            if (!mappings.containsKey(this)) {
                mappings.put(this, scope.reserveType());
            }
            return mappings.get(this);
        } else {
            return this;
        }
    }

    @Override
    protected String toParenthesizedString() {
        return state.toParenthesizedString();
    }

    @Override
    protected String toString_() {
        return state.toString_();
    }

    @Override
    protected Unification unifyWith(TypeVariable query, Scope scope) {
        if (equals(query)) {
            return unified(this);
        } else {
            return bind(query);
        }
    }

    @Override
    protected Unification unifyWith(TypeOperator query, Scope scope) {
        if (query.contains(this)) {
            return recursive(this, query);
        } else {
            return bind(query);
        }
    }

    @Override
    protected Unification unify_(Type target, Scope scope) {
        return target.unifyWith(this, scope);
    }

    private interface State {

        Unification bind(Type type);

        boolean contains(Type type);

        Type expose();

        String getName();

        List<Type> getParameters();

        String toParenthesizedString();

        String toString_();
    }

    private static final class BoundState implements State {

        private final Type type;

        public BoundState(Type type) {
            this.type = type;
        }

        @Override
        public Unification bind(Type type) {
            return failed(this.type, type);
        }

        @Override
        public boolean contains(Type type) {
            return this.type.contains(type);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BoundState && Objects.equals(type, ((BoundState) o).type);
        }

        @Override
        public Type expose() {
            return type.expose();
        }

        @Override
        public String getName() {
            return type.getName();
        }

        @Override
        public List<Type> getParameters() {
            return type.getParameters();
        }

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }

        @Override
        public String toParenthesizedString() {
            return type.toParenthesizedString();
        }

        @Override
        public String toString() {
            return type.toString();
        }

        @Override
        public String toString_() {
            return type.toString_();
        }
    }

    private static final class UnboundState implements State {

        private final TypeVariable parent;
        private final String name;

        public UnboundState(TypeVariable parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public Unification bind(Type type) {
            parent.state = new BoundState(type);
            return unified(type);
        }

        @Override
        public boolean contains(Type type) {
            return parent.equals(type);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof UnboundState && Objects.equals(name, ((UnboundState) o).name);
        }

        @Override
        public Type expose() {
            return parent;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public List<Type> getParameters() {
            return emptyList();
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toParenthesizedString() {
            return toString_();
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public String toString_() {
            return name;
        }
    }
}
