package org.rsrg.mixfix.immutableadts;

/**
 * A sum type used to represent the node types (internal and empty)
 * that also track tree level (needed for representing Arne Andersson (AA) trees).
 * <p>
 * Note: marked private as this type hierarchy is really an implementation of
 * the api for {@link BalancedBst}.
 */
sealed interface AATr<A> {
    final class Empty<A> implements AATr<A> {
        private static final AATr<?> Instance = new Empty<>();

        private Empty() {
        }
    }

    record Node<A>(int lvl, AATr<A> left, A key, AATr<A> right) implements AATr<A> {
    }

    // "smart constructors" for the two node types
    @SuppressWarnings("unchecked") static <T> AATr<T> empty() {
        return (AATr<T>) Empty.Instance;
    }

    static <T> AATr<T> node(int lvl, AATr<T> left, T data, AATr<T> right) {
        return new Node<>(lvl, left, data, right);
    }
}
