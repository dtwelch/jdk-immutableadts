package edu.rsrg.mixfix.immutableadts;

import java.util.Comparator;

// note: this interface is local to this package (used strictly
// as an internal representation tree and set ADTs).
sealed interface AATr<A> {

    final class Empty<A>                                        implements AATr<A> {
        public static final AATr<?> Instance = new AATr.Empty<>();
        private Empty() {}
    }
    record Node<A>(int lvl, AATr<A> left, A key, AATr<A> right) implements AATr<A> {}

    // "smart constructors" for the two node types
    @SuppressWarnings("unchecked") static <T> AATr<T> empty() {
        return (AATr<T>) Empty.Instance;
    }

    static <T> AATr<T> node(int lvl, AATr<T> left, T data, AATr<T> right) {
        return new Node<>(lvl, left, data, right);
    }
}
