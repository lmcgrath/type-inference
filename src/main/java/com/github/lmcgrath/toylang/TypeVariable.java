package com.github.lmcgrath.toylang;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Objects;

public class TypeVariable implements Type {

    public static TypeVariable var(String name) {
        return new TypeVariable(name);
    }

    private State state;

    public TypeVariable(String name) {
        this.state = new UnboundState(this, name);
    }

    @Override
    public void bind(Type type) {
        state.bind(type);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof TypeVariable && Objects.equals(state, ((TypeVariable) o).state);
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
    public int hashCode() {
        return Objects.hash(state);
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public String toString() {
        return state.getName();
    }

    private interface State {

        void bind(Type type);

        Type expose();

        String getName();

        List<Type> getParameters();
    }

    private static final class BoundState implements State {

        private final Type type;

        public BoundState(Type type) {
            this.type = type;
        }

        @Override
        public void bind(Type type) {
            throw new IllegalStateException("Type variable already bound to " + this.type);
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
        public String toString() {
            return type.toString();
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
        public void bind(Type type) {
            parent.state = new BoundState(type);
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
        public String toString() {
            return name;
        }
    }
}
