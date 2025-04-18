package org.rsrg.immutableadts;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * A result either holds value ({@link Ok}) or an error
 * ({@link Err}).
 *
 * @param <T> the type of the value.
 * @param <E> the type of the error.
 */
public sealed interface Result<T, E> {

    // factory methods:
    static <T, E> Result<T, E> ok(T t) {
        return new Ok<>(t);
    }

    static <T, E> Result<T, E> err(E e) {
        return new Err<>(e);
    }

    // instance methods:

    // precondition: this is an instance of {@link Result.Err}.
    default E getError() {
        return switch (this) {
            case Ok(_) -> throw new IllegalArgumentException("precondition violation");
            case Err(var e) -> e;
        };
    }

    /**
     * Returns the success value stored (assuming, as a precondition,
     * {@code this} is an instance of {@link Ok}).
     *
     * @throws IllegalArgumentException if this isn't a success instance.
     */
    default T get() {
        return switch (this) {
            case Ok(var v) -> v;
            case Err(_) -> throw new IllegalArgumentException("precondition violation");
        };
    }

    default boolean isOk() {
        return this instanceof Result.Ok<T, E>;
    }

    default boolean isError() {
        return this instanceof Result.Err<T, E>;
    }

    default <U> Result<U, E> map(Function<T, U> f) {
        return switch (this) {
            case Ok(var t) -> ok(f.apply(t));
            case Err(var e) -> err(e);
        };
    }

    default <B> Result<B, E> flatMap(Function<T, Result<B, E>> f) {
        return switch (this) {
            case Ok(var t) -> f.apply(t);
            case Err(var e) -> err(e);
        };
    }

    default Maybe<T> toMaybe() {
        return switch (this) {
            case Ok(var t) -> Maybe.of(t);
            case Err(_) -> Maybe.none();
        };
    }

    // static/'companion' methods

    /**
     * Evaluates the given results from left to right collecting the values
     * into a list. Returns the first error value encountered, if any.
     */
    static <T, E> Result<VList<T>, E> sequence(VList<Result<T, E>> xs) {
        var result = new ArrayList<T>();
        for (var res : xs.reverse()) {
            switch (res) {
                case Ok(var r) -> result.add(r);
                case Err(_) -> {
                }
            }
        }
        return ok(VList.ofAll(result));
    }

    /**
     * Applies {@code f} to each element in the list. Fails at the first error
     * found, or returns the new list.
     */
    static <T, S, E> Result<VList<S>, E> traverse(Iterable<T> xs, Function<T, Result<S, E>> f) {
        var result = new ArrayList<S>();
        for (T x : xs) {
            switch (f.apply(x)) {
                // case 1: ok, add to the list
                case Ok(var ok) -> result.add(ok);
                // case 2: error, short-circuit
                case Err(var e) -> {
                    return err(e);
                }
            }
        }
        return ok(VList.ofAll(result));
    }

    // actual implementations:

    record Ok<T, E>(T t) implements Result<T, E> {
    }

    record Err<T, E>(E e) implements Result<T, E> {
    }
}
