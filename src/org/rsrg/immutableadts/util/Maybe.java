package org.rsrg.immutableadts.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/** A maybe/option type modeled after the one found in haskell. */
public sealed interface Maybe<A> {

    // factory methods
    static <T> Maybe<T> of(T value) {
        return (value == null) ? none() : new Some<>(value);
    }

    @SuppressWarnings("unchecked") static <T> Maybe<T> none() {
        return (None<T>) None.Instance;
    }

    default A getOrElse(A other) {
        return isEmpty() ? other : get();
    }

    default A getOrElse(Supplier<A> supplier) {
        return isEmpty() ? supplier.get() : get();
    }

    A get();

    default boolean isEmpty() {
        return this instanceof Maybe.None<A>;
    }

    default boolean nonEmpty() {
        return this instanceof Maybe.Some<A>;
    }

    default boolean isDefined() {
        return nonEmpty();
    }

    default boolean contains(A item) {
        return switch (this) {
            case Some(var x) -> x.equals(item);
            case None<A> _ -> false;
        };
    }

    final class None<A> implements Maybe<A> {
        public static final None<?> Instance = new None<>();

        private None() {}

        @Override public A get() {
            throw new NoSuchElementException("option is empty");
        }

        @Override public int hashCode() {
            return 1;
        }
    }

    record Some<A>(A value) implements Maybe<A> {
        @Override public A get() {
            return value;
        }

        @Override public boolean equals(Object o) {
            return switch (o) {
                case Maybe.Some(var ov) -> this.value.equals(ov);
                default                 -> false;
            };
        }

        @Override public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    // remember: fmap is functorMap (not flatMap -- which would return an obj)
    // really: since we're in java, would be better to probably just rename
    // this 'map' as we're not/can't model functors realistically here`
    default <B> Maybe<B> map(Function<A, B> f) {
        Objects.requireNonNull(f, "fn is null");

        return switch (this) {
            case Maybe.None<?> _ -> none();
            case Some(var x) -> new Some<>(f.apply(x));
        };
    }
}