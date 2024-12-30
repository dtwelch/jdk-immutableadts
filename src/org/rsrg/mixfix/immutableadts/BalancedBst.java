package org.rsrg.mixfix.immutableadts;

import java.util.Comparator;
import java.util.function.BiFunction;

/**
 * An immutable BST that guarantees an O(log n) worst case runtime
 * for standard operations (e.g., insert, delete, contains, etc.).
 * <p>
 * Here, {@link AATr} is the underlying algebraic type we use as the
 * representation for our balanced search tree. Its current state is
 * tracked in the {@code rep} field.
 * <p>
 * <b>Rep invariant:</b> {@code rep} always adheres to invariants A1-A4
 * (see linked paper).
 * <p>
 * Note: clients can obtain a tree instance via the factory methods
 * {@link #empty()} and {@link #of(Comparable[])}.
 *
 * @param <A> the type stored within the nodes of this tree.
 */
public final class BalancedBst<A> {
    private final Comparator<A> order;
    private final AATr<A> rep;

    private BalancedBst(Comparator<A> order, AATr<A> rep) {
        this.order = order;
        this.rep = rep;
    }

    // factory methods:

    static <T> BalancedBst<T> empty(Comparator<T> o) {
        return new BalancedBst<>(o, AATr.empty());
    }

    static <T extends Comparable<T>> BalancedBst<T> empty() {
        return empty(Comparable::compareTo);
    }

    @SafeVarargs static <T> BalancedBst<T> of(Comparator<T> o, T... ts) {
        var result = empty(o);
        for (var t : ts) {
            result = result.insert(t);
        }
        return result;
    }

    @SafeVarargs static <T extends Comparable<T>> BalancedBst<T> of(T... ts) {
        return of(Comparable::compareTo, ts);
    }

    // core operations:

    /** O(log n) - inserts {@code key} into this tree with balancing. */
    public BalancedBst<A> insert(A key) {
        var updatedRep = insert(key, rep);
        return new BalancedBst<>(order, updatedRep);
    }

    // recursive helper:
    private AATr<A> insert(A k, AATr<A> t) {
        return switch (t) {
            case AATr.Empty<A> _ -> AATr.node(1, AATr.empty(), k, AATr.empty());
            case AATr.Node(var trLvl, var a, var trKey, var b) when order.compare(k, trKey) < 0 -> {
                var rawLeft = insert(k, a);
                var nodeToSkew = AATr.node(trLvl, rawLeft, trKey, b);
                var skewedLeft = skew(nodeToSkew);
                var splitLeft = split(skewedLeft);
                yield splitLeft;
            }
            case AATr.Node(var trLvl, var a, var trKey, var b) when order.compare(k, trKey) > 0 -> {
                var rawRight = insert(k, b);
                var nodeToSkew = AATr.node(trLvl, a, trKey, rawRight);
                var skewedRight = skew(nodeToSkew);
                var splitRight = split(skewedRight);
                yield splitRight;
            }
            // case 3: key == another already in the tree, return the
            // tree unchanged (we don't deal with dups for now)
            case AATr<A> _ -> rep;
        };
    }


    /**
     * O(1) - an initial fixup operation (the result of which sometimes needs to
     * be fixed up further via `skew`). Anyways, idea with this transform is:
     * <pre><code>
     *     y <-- x               y --> x
     *    / \     \   =skew=>   /     / \
     *   a   b     c            a     b  c
     * </code></pre>
     */
    private AATr<A> skew(AATr<A> t) {
        return switch (t) {
            //@formatter:off
            case AATr.Node(var xLvl,
                           AATr.Node(var yLvl, var a, var yKey, var b),
                           var xKey,
                           var c
            //@formatter:on
            ) when xLvl == yLvl -> AATr.node(xLvl, a, yKey, AATr.node(xLvl, b, xKey, c));
            case AATr<A> _ -> t;
        };
    }

    /**
     * O(1) - fixup operation in the event that the root of the tree {@code t} previously
     * skewed had a right child at the same level.
     */
    private AATr<A> split(AATr<A> t) {
        return switch (t) { //@formatter:off
            case AATr.Node(var xLvl,
                           AATr.Node(var yLvl, var a, var yKey, var b),
                           var xKey,
                           var c) when xLvl == yLvl ->
                    //@formatter:on
                    AATr.node(xLvl, a, yKey, AATr.node(xLvl, b, xKey, c));
            case AATr<A> _ -> t;
        };
    }

    /** O(n) - returns the number of nodes in this tree. */
    public int size() {
        return fold(rep, 0, (acc, _) -> acc + 1);
    }

    /**
     * O(n) - performs a (left) fold over the data stored in the nodes of
     * this tree using the provided binary function {@code f}.
     */
    private <B> B fold(AATr<A> t, B neutral,
                       BiFunction<B, A, B> f) {
        return switch (t) {
            case AATr.Node(_, var a, var k, var b) -> {
                var leftVal = fold(a, neutral, f);
                var updatedRootVal = f.apply(leftVal, k);
                var rightVal = fold(b, updatedRootVal, f);
                yield rightVal;
            }
            case AATr.Empty<A> _ -> neutral;
        };
    }

    /**
     * A sum type used to represent the node types (internal and empty)
     * that also track tree level (needed for representing Arne Andersson (AA) trees).
     * <p>
     * Note: marked private as this type hierarchy is really an implementation of
     * the api for {@link BalancedBst}.
     */
    protected sealed interface AATr<A> {
        final class Empty<A> implements AATr<A> {
            public static final AATr<?> Instance = new Empty<>();

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
}
