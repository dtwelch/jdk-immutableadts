package org.rsrg.mixfix.immutableadts;

import java.util.Comparator;

/**
 * An immutable BST that guarantees an O(log n) worst case runtime
 * for insert, delete, contains, etc.
 *
 * @param <A> the type stored within the nodes of this tree.
 */
public final class BalancedBst<A> {

    /**
     * The underlying algebraic type we use as the representation
     * for our balanced search tree.
     * <p>
     * <b>Rep invariant:</b> {@code repTree} always adheres to
     * invariants A1-A4 (see linked paper).
     */
    private final AATr<A> repTree;
    private final Comparator<A> o;

    private BalancedBst(Comparator<A> order, AATr<A> rep) {
        this.repTree = rep;
        this.o = order;
    }

    static <T> BalancedBst<T> empty(Comparator<T> o) {
        return new BalancedBst<>(o, AATr.empty());
    }

    static <T extends Comparable<T>> BalancedBst<T> empty() {
        return empty(Comparable::compareTo);
    }

    // core operations

    /** O(log n) - inserts {@code key} into this tree with balancing. */
    public BalancedBst<A> insert(A key) {
        var updatedRepTree = switch (repTree) {
            case AATr.Empty<A> _ -> AATr.node(1, AATr.empty(), key, AATr.empty());
            case AATr.Node(var lvl, var a, var trKey, var b)
                    when o.compare(key, trKey) < 0 -> {
               throw new RuntimeException("not done");
            }
            case AATr.Node(var lvl, var a, var trKey, var b)
                    when o.compare(key, trKey) > 0 -> {
                throw new RuntimeException("not done");
            }
            // case 3: key == another already in the tree, return the
            // tree unchanged (we don't deal with dups for now)
            case AATr<A> _ -> repTree;
        };
        return new BalancedBst<>(o, updatedRepTree);
    }

    private sealed interface AATr<A> {
        final class Empty<A>                                        implements AATr<A> {
            public static final AATr<?> Instance = new Empty<>();
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
}
