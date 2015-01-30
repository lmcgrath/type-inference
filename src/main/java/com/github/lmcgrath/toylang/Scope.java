package com.github.lmcgrath.toylang;

import static com.github.lmcgrath.toylang.unification.Unifications.unified;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import com.github.lmcgrath.toylang.type.Type;
import com.github.lmcgrath.toylang.type.TypeVariable;
import com.github.lmcgrath.toylang.unification.Unification;
import com.google.common.collect.ImmutableSet;

public class Scope {

    private final State state;

    public Scope() {
        state = new HeadState();
    }

    private Scope(Scope parent) {
        state = new TailState(parent);
    }

    public Unification bind(TypeVariable variable, Type type) {
        return state.bind(variable, type);
    }

    public void define(String id, Type type) {
        state.define(id, type);
    }

    public void error(Unification unification) {
        state.error(unification);
    }

    public Type expose(TypeVariable variable) {
        return state.expose(variable);
    }

    public void generify(Type type) {
        state.generify(type);
    }

    public Set<Unification> getErrors() {
        return state.getErrors();
    }

    public boolean isDefined(String id) {
        return state.isDefined(id);
    }

    public boolean isGeneric(Type type) {
        return !occursIn(type, state.getSpecializedTypes());
    }

    public Type reserveType() {
        return state.reserveType();
    }

    public <T> T scoped(Function<Scope, T> function) {
        return function.apply(new Scope(this));
    }

    public void specialize(Type type) {
        state.specialize(type);
    }

    public Optional<Type> typeOf(String id) {
        return state.getType(id)
            .map(type -> type.genericCopy(this));
    }

    private Set<Type> getSpecializedTypes() {
        return state.getSpecializedTypes();
    }

    private boolean occursIn(Type variable, Collection<Type> types) {
        for (Type type : types) {
            if (occursIn_(variable, type)) {
                return true;
            }
        }
        return false;
    }

    private boolean occursIn_(Type variable, Type type) {
        return variable.equals(type) || occursIn(variable, type.getParameters());
    }

    boolean occursIn(Type variable, Type type) {
        return occursIn_(variable.expose(this), type.expose(this));
    }

    private interface State {

        Unification bind(TypeVariable variable, Type type);

        void define(String id, Type type);

        void error(Unification unification);

        Type expose(TypeVariable variable);

        void generify(Type type);

        Set<Unification> getErrors();

        Set<Type> getSpecializedTypes();

        Optional<Type> getType(String id);

        boolean isDefined(String id);

        Type reserveType();

        void specialize(Type type);
    }

    private final class HeadState implements State {

        private final Map<Type, Type>   bindings;
        private final Map<String, Type> symbols;
        private final Set<Type>         specializedTypes;
        private final Set<Unification>  errors;
        private       int               nameFlips;
        private       char              nextName;

        public HeadState() {
            bindings = new HashMap<>();
            symbols = new HashMap<>();
            specializedTypes = new HashSet<>();
            errors = new HashSet<>();
            nameFlips = -1;
            nextName = 'a';
        }

        @Override
        public Unification bind(TypeVariable variable, Type type) {
            if (bindings.containsKey(variable)) {
                return bindings.get(variable).unify(type, Scope.this);
            } else {
                bindings.put(variable, type);
                return unified(type);
            }
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
        public void error(Unification unification) {
            errors.add(unification);
        }

        @Override
        public Type expose(TypeVariable variable) {
            if (bindings.isEmpty()) {
                return variable;
            } else {
                Type result = variable;
                while (bindings.containsKey(result)) {
                    result = bindings.get(result).expose(Scope.this);
                }
                return result;
            }
        }

        @Override
        public void generify(Type type) {
            specializedTypes.remove(type);
        }

        @Override
        public Set<Unification> getErrors() {
            return ImmutableSet.copyOf(errors);
        }

        @Override
        public Set<Type> getSpecializedTypes() {
            return new HashSet<>(specializedTypes);
        }

        @Override
        public Optional<Type> getType(String id) {
            return Optional.ofNullable(symbols.get(id));
        }

        @Override
        public boolean isDefined(String id) {
            return symbols.containsKey(id);
        }

        @Override
        public Type reserveType() {
            String name = "" + nextName++ + (nameFlips > -1 ? nameFlips : "");
            if (name.equals("z")) {
                nextName = 'a';
                nameFlips++;
            }
            return new TypeVariable(name);
        }

        @Override
        public void specialize(Type type) {
            specializedTypes.add(type);
        }
    }

    private static final class TailState implements State {

        private final Scope             parent;
        private final Map<String, Type> symbols;
        private final Set<Type>         specialized;

        public TailState(Scope parent) {
            this.parent = parent;
            this.symbols = new HashMap<>();
            this.specialized = new HashSet<>();
        }

        @Override
        public Unification bind(TypeVariable variable, Type type) {
            return parent.bind(variable, type);
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
        public void error(Unification unification) {
            parent.error(unification);
        }

        @Override
        public Type expose(TypeVariable variable) {
            return parent.expose(variable);
        }

        @Override
        public void generify(Type type) {
            specialized.remove(type);
            parent.generify(type);
        }

        @Override
        public Set<Unification> getErrors() {
            return parent.getErrors();
        }

        @Override
        public Set<Type> getSpecializedTypes() {
            Set<Type> specifics = new HashSet<>();
            specifics.addAll(this.specialized);
            specifics.addAll(parent.getSpecializedTypes());
            return specifics;
        }

        @Override
        public Optional<Type> getType(String id) {
            Optional<Type> type = Optional.ofNullable(symbols.get(id));
            if (type.isPresent()) {
                return type;
            } else {
                return parent.typeOf(id);
            }
        }

        @Override
        public boolean isDefined(String id) {
            return isDefinedLocally(id) || parent.isDefined(id);
        }

        @Override
        public Type reserveType() {
            return parent.reserveType();
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
