package edu.rsrg.mixfix.immutableadts;

import java.util.Comparator;

/**
 * An immutable tree map that guarantees O(log n) runtime for all
 * standard map modification/transformation operations.
 *
 * @param <A> the type stored within the nodes of this tree.
 */
public final class VTreeMap<A> {

    /**
     * The underlying algebraic type we use as the representation
     * for our balanced search tree.
     * <p>
     * <b>Rep invariant:</b> {@code repTree} always adheres to
     * invariants A1-A4 (see linked paper).
     */
    private final AATr<A> repTree;
    private final Comparator<A> o;

    private VTreeMap(Comparator<A> order, AATr<A> rep) {
        this.repTree = rep;
        this.o = order;
    }

    static <T> VTreeMap<T> empty(Comparator<T> o) {
        return new VTreeMap<>(o, AATr.empty());
    }

    static <T extends Comparable<T>> VTreeMap<T> empty() {
        return empty(Comparable::compareTo);
    }

    // core operations

    /** O(log n) - inserts {@code key} into this tree with balancing. */
    public VTreeMap<A> insert(A key) {
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
        return new VTreeMap<>(o, updatedRepTree);
    }
}
