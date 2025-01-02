package org.rsrg.mixfix.immutableadts;

/**
 * A sum type used to represent the node types (internal and empty)
 * that also track tree level (needed for representing Arne Andersson (AA) trees).
 * <p>
 * Note: marked private as this type hierarchy is really an implementation of
 * the api for {@link BalancedBst}.
 */
sealed interface AlgebraicTr<A> {
    final class Empty<A> implements AlgebraicTr<A> {
        private static final AlgebraicTr<?> Instance = new Empty<>();

        private Empty() {
        }
    }

    record Node<A>(int lvl, AlgebraicTr<A> left, A key, AlgebraicTr<A> right) implements AlgebraicTr<A> {
    }

    // "smart constructors" for the two node types
    @SuppressWarnings("unchecked") static <T> AlgebraicTr<T> empty() {
        return (AlgebraicTr<T>) Empty.Instance;
    }

    static <T> AlgebraicTr<T> node(int lvl, AlgebraicTr<T> left, T data, AlgebraicTr<T> right) {
        return new Node<>(lvl, left, data, right);
    }
}
