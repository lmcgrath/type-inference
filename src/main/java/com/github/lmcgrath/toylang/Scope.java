package com.github.lmcgrath.toylang;

import static java.lang.Character.isLetter;
import static java.lang.Character.isUpperCase;
import static com.github.lmcgrath.toylang.unification.Unifications.mismatch;
import static com.github.lmcgrath.toylang.unification.Unifications.recursive;
import static com.github.lmcgrath.toylang.unification.Unifications.unified;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.github.lmcgrath.toylang.type.Type;
import com.github.lmcgrath.toylang.type.TypeOperator;
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

    public Set<Unification> getErrors() {
        return state.getErrors();
    }

    public Type reserveType() {
        return state.createVariable();
    }

    public void define(String id, Type type) {
        state.define(id, type);
    }

    public void error(Unification unification) {
        state.error(unification);
    }

    public Scope extend() {
        return new Scope(this);
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

    public Optional<Type> typeOf(String id) {
        return state.getType(id)
            .map(type -> genericCopy(type, new HashMap<>()));
    }

    public Unification unify(Type target, Type query) {
        return unify_(target.expose(), query.expose());
    }

    private Type genericCopy(Type type, HashMap<Type, Type> mappings) {
        Type actualType = type.expose();
        if (actualType.isVariable()) {
            if (isGeneric(actualType)) {
                if (!mappings.containsKey(actualType)) {
                    mappings.put(actualType, reserveType());
                }
                return mappings.get(actualType);
            } else {
                return actualType;
            }
        } else {
            List<Type> parameters = actualType.getParameters().stream()
                .map(parameter -> genericCopy(parameter, mappings))
                .collect(Collectors.toList());
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
            if (occursIn_(variable, type)) {
                return true;
            }
        }
        return false;
    }

    private boolean occursIn_(Type variable, Type type) {
        return variable.equals(type) || occursIn(variable, type.getParameters());
    }

    private Unification unifyParameters(Type target, Type query) {
        List<Type> targetParams = target.getParameters();
        List<Type> queryParams = query.getParameters();
        if (target.getName().equals(query.getName()) && targetParams.size() == queryParams.size()) {
            List<Type> unifiedParams = new ArrayList<>();
            for (int i = 0; i < targetParams.size(); i++) {
                Unification unification = unify(targetParams.get(i), queryParams.get(i));
                unification.ifUnified(unifiedParams::add);
                if (!unification.isUnified()) {
                    return unification;
                }
            }
            return unified(new TypeOperator(target.getName(), unifiedParams));
        } else {
            return mismatch(target, query);
        }
    }

    private Unification unify_(Type target, Type query) {
        if (target.isVariable()) {
            if (target.equals(query)) {
                return unified(target);
            } else {
                if (occursIn_(target, query)) {
                    return recursive(query, target);
                } else {
                    return target.bind(query);
                }
            }
        } else if (query.isVariable()) {
            return unify(query, target);
        } else {
            return unifyParameters(target, query);
        }
    }

    boolean occursIn(Type variable, Type type) {
        return occursIn_(variable.expose(), type.expose());
    }

    private interface State {

        Type createVariable();

        void define(String id, Type type);

        void error(Unification unification);

        void generify(Type type);

        Set<Unification> getErrors();

        Set<Type> getSpecializedTypes();

        Optional<Type> getType(String id);

        boolean isDefined(String id);

        void specialize(Type type);
    }

    private static final class HeadState implements State {

        private final Map<String, Type> symbols;
        private final Set<Type>         specializedTypes;
        private final Set<Unification>  errors;
        private char nextName = 'a';

        public HeadState() {
            symbols = new HashMap<>();
            specializedTypes = new HashSet<>();
            errors = new HashSet<>();
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
        public void error(Unification unification) {
            errors.add(unification);
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
        public Type createVariable() {
            return parent.reserveType();
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
        public void specialize(Type type) {
            specialized.add(type);
        }

        private boolean isDefinedLocally(String id) {
            return symbols.containsKey(id);
        }
    }
}
