package com.github.lmcgrath.toylang;

import static java.lang.Character.isLetter;
import static java.lang.Character.isUpperCase;

import java.util.*;

public class TypeEnvironment {

    private final State state;

    public TypeEnvironment() {
        state = new HeadState();
    }

    private TypeEnvironment(TypeEnvironment parent) {
        state = new TailState(parent);
    }

    public Type createVariable() {
        return state.createVariable();
    }

    public void define(String id, Type type) {
        state.define(id, type);
    }

    public TypeEnvironment extend() {
        return new TypeEnvironment(this);
    }

    public void generify(Type type) {
        state.generify(type);
    }

    public boolean isDefined(String id) {
        return state.isDefined(id);
    }

    public void specialize(Type type) {
        state.specialize(type);
    }

    public Type typeOf(String id) throws TypeException {
        return genericCopy(state.getType(id), new HashMap<Type, Type>());
    }

    public void unify(Type left, Type right) throws TypeException {
        Type a = left.expose();
        Type b = right.expose();
        if (a.isVariable()) {
            if (!a.equals(b)) {
                if (occursIn(a, b)) {
                    throw new TypeException("Recursive unification: " + a + " != " + b);
                } else {
                    a.bind(b);
                }
            }
        } else if (b.isVariable()) {
            unify(b, a);
        } else {
            unifyParameters(a, b);
        }
    }

    private Type genericCopy(Type type, HashMap<Type, Type> mappings) {
        Type actualType = type.expose();
        if (actualType.isVariable()) {
            if (isGeneric(actualType)) {
                if (!mappings.containsKey(actualType)) {
                    mappings.put(actualType, createVariable());
                }
                return mappings.get(actualType);
            } else {
                return actualType;
            }
        } else {
            List<Type> parameters = new ArrayList<>();
            for (Type parameter : actualType.getParameters()) {
                parameters.add(genericCopy(parameter, mappings));
            }
            return new TypeOperator(actualType.getName(), parameters);
        }
    }

    private Set<Type> getSpecializedTypes() {
        return state.getSpecializedTypes();
    }

    private boolean isGeneric(Type type) {
        return !occursIn(type, state.getSpecializedTypes());
    }

    private boolean occursIn(Type variable, Collection<Type> types) {
        for (Type type : types) {
            if (occursIn(variable, type)) {
                return true;
            }
        }
        return false;
    }

    private void unifyParameters(Type left, Type right) throws TypeException {
        List<Type> leftParameters = left.getParameters();
        List<Type> rightParameters = right.getParameters();
        if (left.getName().equals(right.getName()) && leftParameters.size() == rightParameters.size()) {
            for (int i = 0; i < leftParameters.size(); i++) {
                unify(leftParameters.get(i), rightParameters.get(i));
            }
        } else {
            throw new TypeException("Type mismatch: " + left + " != " + right);
        }
    }

    boolean occursIn(Type variable, Type type) {
        Type actualVariable = variable.expose();
        Type actualType = type.expose();
        return actualVariable.equals(actualType) || occursIn(actualVariable, actualType.getParameters());
    }

    private interface State {

        Type createVariable();

        void define(String id, Type type);

        void generify(Type type);

        Set<Type> getSpecializedTypes();

        Type getType(String id) throws TypeException;

        boolean isDefined(String id);

        void specialize(Type type);
    }

    private static final class HeadState implements State {

        private final Map<String, Type> symbols;
        private final Set<Type> specializedTypes;
        private char nextName = 'a';

        public HeadState() {
            symbols = new HashMap<>();
            specializedTypes = new HashSet<>();
        }

        @Override
        public Type createVariable() {
            char name = nextName++;
            while (isUpperCase(name) || !isLetter(name)) {
                name = nextName++;
                if (name >= Character.MAX_VALUE) {
                    throw new IllegalStateException("Ran out of names!");
                }
            }
            return new TypeVariable(String.valueOf(name));
        }

        @Override
        public void define(String id, Type type) {
            if (isDefined(id)) {
                throw new IllegalStateException("Type " + id + " already defined");
            } else {
                symbols.put(id, type);
            }
        }

        @Override
        public void generify(Type type) {
            specializedTypes.remove(type);
        }

        @Override
        public Set<Type> getSpecializedTypes() {
            return new HashSet<>(specializedTypes);
        }

        @Override
        public Type getType(String id) throws TypeException {
            if (isDefined(id)) {
                return symbols.get(id);
            } else {
                throw new UndefinedSymbolException("Undefined symbol: " + id);
            }
        }

        @Override
        public boolean isDefined(String id) {
            return symbols.containsKey(id);
        }

        @Override
        public void specialize(Type type) {
            specializedTypes.add(type);
        }
    }

    private static final class TailState implements State {

        private final TypeEnvironment parent;
        private final Map<String, Type> symbols;
        private final Set<Type> specialized;

        public TailState(TypeEnvironment parent) {
            this.parent = parent;
            this.symbols = new HashMap<>();
            this.specialized = new HashSet<>();
        }

        @Override
        public Type createVariable() {
            return parent.createVariable();
        }

        @Override
        public void define(String id, Type type) {
            if (isDefinedLocally(id)) {
                throw new IllegalStateException("Type " + id + " already defined");
            } else {
                symbols.put(id, type);
            }
        }

        @Override
        public void generify(Type type) {
            specialized.remove(type);
            parent.generify(type);
        }

        @Override
        public Set<Type> getSpecializedTypes() {
            Set<Type> specifics = new HashSet<>();
            specifics.addAll(this.specialized);
            specifics.addAll(parent.getSpecializedTypes());
            return specifics;
        }

        @Override
        public Type getType(String id) throws TypeException {
            if (isDefinedLocally(id)) {
                return symbols.get(id);
            } else {
                return parent.typeOf(id);
            }
        }

        @Override
        public boolean isDefined(String id) {
            return isDefinedLocally(id) || parent.isDefined(id);
        }

        @Override
        public void specialize(Type type) {
            specialized.add(type);
        }

        private boolean isDefinedLocally(String id) {
            return symbols.containsKey(id);
        }
    }
}
