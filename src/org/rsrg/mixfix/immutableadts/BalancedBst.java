package org.rsrg.mixfix.immutableadts;

import org.rsrg.mixfix.util.Maybe;
import org.rsrg.mixfix.util.Pair;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;
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
final class BalancedBst<A> implements Iterable<A> {
    private final Comparator<A> order;
    final AATr<A> rep;

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

    /**
     * O(log n) - inserts {@code key} into this tree with balancing;
     * returns a pair: (resulting-tree, was-inserted).
     */
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
            case AATr<A> _ -> t;
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

    public Maybe<A> find(A key) {
        return find(key, rep);
    }

    private Maybe<A> find(A key, AATr<A> t) {
        return switch (t)  {
            case AATr.Node(_, var a, var k, _) when order.compare(key, k) < 0  -> find(key, a);
            case AATr.Node(_, _, var k, var b) when order.compare(key, k) > 0  -> find(key, b);
            case AATr.Node(_, _, var k, var _) when order.compare(key, k) == 0 -> Maybe.of(k);
            default -> Maybe.none();
        };
    }

    /** O(n) - returns the number of nodes in this tree. */
    public int size() {
        return fold(rep, 0, (acc, _) -> acc + 1);
    }

    /**
     * O(log n) - returns true only if {@code key} appears in this
     * tree; false otherwise.
     */
    public boolean contains(A key) {
        return contains(key, rep);
    }

    private boolean contains(A key, AATr<A> t) {
        return switch (t) {
            case AATr.Node(_, var a, var k, _) when order.compare(key, k) < 0 -> contains(key, a);
            case AATr.Node(_, _, var k, var b) when order.compare(key, k) > 0 -> contains(key, b);
            case AATr.Node(_, _, _, _)  -> true;
            case AATr.Empty<A> _        -> false;
        };
    }

    /**
     * O(1) - returns true only if this tree is empty (has no nodes);
     * false otherwise.
     */
    public boolean null_() {
        return switch (rep) {
            case AATr.Empty<A> _ -> true;
            default              -> false;
        };
    }

    /**
     * O(n) - performs a (left) fold over the data stored in the nodes of
     * this tree using the provided binary function {@code f}.
     */
    public <B> B fold(BalancedBst<A> t, B neutral,
                       BiFunction<B, A, B> f) {
        var result = fold(t.rep, neutral, f);
        return result;
    }

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
     * O(log n) - deletes a key-value pair from this tree; returns
     * a pair (resulting-tree, was-deleted).
     */
    public BalancedBst<A> delete(A key) {
        var updatedRep = delete(key, rep);
        throw new UnsupportedOperationException("not done");
    }

    // returns a (tree-post-deletion : AATr<A>, keyRemoved : boolean)
    private Pair<AATr<A>, Boolean> delete(A key, AATr<A> t) {
        throw new UnsupportedOperationException("not done");
        /*return switch (t) {
            case AATr.Empty<A> e -> e;
            case AATr.Node(_, AATr.Empty<A> _, _, var rt) -> rt;
            case AATr.Node(_, var lt, _, AATr.Empty<A> _) -> lt;
            case AATr.Node(_, var lt, var k, var rt) ->
        };*/
    }

    @Override public Iterator<A> iterator() {
        return new BstIter();
    }

    private final class BstIter implements Iterator<A> {
        /** Simulates the recursive call stack to avoid recursion. */
        private final Stack<AATr<A>> stack = new Stack<>();
        private A nextElement;

        public BstIter() {
            pushLeft(rep);
            advance(); // init nextElement
        }

        private void pushLeft(AATr<A> node) {
            while (node instanceof AATr.Node<A> n) {
                stack.push(n);
                node = n.left();
            }
        }

        /** Advances to the next element in the traversal. */
        private void advance() {
            if (stack.isEmpty()) {
                nextElement = null;
                return;
            }

            var node = stack.pop();
            switch (node) {
                case AATr.Node(_, var a, var k, var b) -> {
                    pushLeft(b);
                    nextElement = k;
                }
                case AATr.Empty<A> _ -> nextElement = null;
            }
        }

        @Override public boolean hasNext() {
            return nextElement != null;
        }

        @Override public A next() {
            if (nextElement == null) {
                throw new NoSuchElementException("no more elements in the tree.");
            }
            var result = nextElement;
            advance();
            return result;
        }
    }
}
