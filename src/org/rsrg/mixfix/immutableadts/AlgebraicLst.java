package org.rsrg.mixfix.immutableadts;

/**
 * A sum type used to represent a singly linked list.
 *
 * @param <A> the type of data stored in each node.
 */
public sealed interface AlgebraicLst<A> {

    final class Empty<A> implements AlgebraicLst<A> {
        private static final AlgebraicLst<?> Instance = new Empty<>();

        private Empty() {
        }
    }

    record NonEmpty<A>(A head,
                       AlgebraicLst<A> rest) implements AlgebraicLst<A> {
    }

    // "smart constructors" for the two types of lists
    @SuppressWarnings("unchecked") static <T> AlgebraicLst<T> empty() {
        return (AlgebraicLst<T>) Empty.Instance;
    }

    static <T> AlgebraicLst<T> cons(T item, AlgebraicLst<T> next) {
        return new AlgebraicLst.NonEmpty<>(item, next);
    }
}
