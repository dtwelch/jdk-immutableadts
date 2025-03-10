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
 * Here, {@link AlgebraicTr} is the underlying algebraic type we use as the
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
    final AlgebraicTr<A> rep;

    private BalancedBst(Comparator<A> order, AlgebraicTr<A> rep) {
        this.order = order;
        this.rep = rep;
    }

    // factory methods:

    static <T> BalancedBst<T> empty(Comparator<T> o) {
        return new BalancedBst<>(o, AlgebraicTr.empty());
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
    private AlgebraicTr<A> insert(A k, AlgebraicTr<A> t) {
        return switch (t) {
            case AlgebraicTr.Empty<A> _ -> AlgebraicTr.node(1, AlgebraicTr.empty(), k, AlgebraicTr.empty());
            case AlgebraicTr.Node(var trLvl, var a, var trKey, var b) when order.compare(k, trKey) < 0 -> {
                var rawLeft = insert(k, a);
                var nodeToSkew = AlgebraicTr.node(trLvl, rawLeft, trKey, b);
                var skewedLeft = skew(nodeToSkew);
                var splitLeft = split(skewedLeft);
                yield splitLeft;
            }
            case AlgebraicTr.Node(var trLvl, var a, var trKey, var b) when order.compare(k, trKey) > 0 -> {
                var rawRight = insert(k, b);
                var nodeToSkew = AlgebraicTr.node(trLvl, a, trKey, rawRight);
                var skewedRight = skew(nodeToSkew);
                var splitRight = split(skewedRight);
                yield splitRight;
            }
            // case 3: key == another already in the tree, return the
            // tree unchanged (we don't deal with dups for now)
            case AlgebraicTr<A> _ -> t;
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
    private AlgebraicTr<A> skew(AlgebraicTr<A> t) {
        return switch (t) {
            //@formatter:off
            case AlgebraicTr.Node(var xLvl,
                           AlgebraicTr.Node(var yLvl, var a, var yKey, var b),
                           var xKey,
                           var c
            //@formatter:on
            ) when xLvl == yLvl -> AlgebraicTr.node(xLvl, a, yKey, AlgebraicTr.node(xLvl, b, xKey, c));
            case AlgebraicTr<A> _ -> t;
        };
    }

    /**
     * O(1) - fixup operation in the event that the root of the tree {@code t} previously
     * skewed had a right child at the same level.
     */
    private AlgebraicTr<A> split(AlgebraicTr<A> t) {
        return switch (t) { //@formatter:off
            case AlgebraicTr.Node(var xLvl,
                           var a,
                           var xKey,
                           AlgebraicTr.Node(var yLvl, var b, var yKey,
                                            AlgebraicTr.Node(var zLvl, var c, var zKey, var d))
                           ) when xLvl == yLvl && yLvl == zLvl ->
                    //@formatter:on
                    AlgebraicTr.node(xLvl + 1, AlgebraicTr.node(xLvl, a, xKey, b), yKey,
                            AlgebraicTr.node(yLvl, c, zKey, d));
            case AlgebraicTr<A> _ -> t;
        };
    }

    public Maybe<A> find(A key) {
        return find(key, rep);
    }

    private Maybe<A> find(A key, AlgebraicTr<A> t) {
        return switch (t)  {
            case AlgebraicTr.Node(_, var a, var k, _) when order.compare(key, k) < 0  -> find(key, a);
            case AlgebraicTr.Node(_, _, var k, var b) when order.compare(key, k) > 0  -> find(key, b);
            case AlgebraicTr.Node(_, _, var k, var _) when order.compare(key, k) == 0 -> Maybe.of(k);
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

    private boolean contains(A key, AlgebraicTr<A> t) {
        return switch (t) {
            case AlgebraicTr.Node(_, var a, var k, _) when order.compare(key, k) < 0 -> contains(key, a);
            case AlgebraicTr.Node(_, _, var k, var b) when order.compare(key, k) > 0 -> contains(key, b);
            case AlgebraicTr.Node(_, _, _, _)  -> true;
            case AlgebraicTr.Empty<A> _        -> false;
        };
    }

    /**
     * O(1) - returns true only if this tree is empty (has no nodes);
     * false otherwise.
     */
    public boolean null_() {
        return switch (rep) {
            case AlgebraicTr.Empty<A> _ -> true;
            default                     -> false;
        };
    }

    /**
     * O(n) - performs a (left) fold over the data stored in the nodes of
     * this tree using the provided binary function {@code f}.
     * <p>
     * Note: assuming {@link #insert} is correct (re: enforcing
     * invariants A1-A4), the depth of the recursive call stack for any
     * operation on balanced bst {@code rep} will not exceed log n (where
     * n=total # of nodes).
     * <p>
     * This is in contrast to a functional linked list where we can't pop the
     * stack until we've reached the empty node at the end of the list (after
     * pushing frames for all N preceding nodes...). But since we're balanced
     * here, we get to pop the stack more often -- there will never be all n
     * nodes on the call stack at once.
     */
    public <B> B fold(BalancedBst<A> t, B neutral,
                       BiFunction<B, A, B> f) {
        var result = fold(t.rep, neutral, f);
        return result;
    }

    private <B> B fold(AlgebraicTr<A> t, B neutral, BiFunction<B, A, B> f) {
        return switch (t) {
            case AlgebraicTr.Node(_, var a, var k, var b) -> {
                var leftVal = fold(a, neutral, f);
                var updatedRootVal = f.apply(leftVal, k);
                var rightVal = fold(b, updatedRootVal, f);
                yield rightVal;
            }
            case AlgebraicTr.Empty<A> _ -> neutral;
        };
    }

    /**
     * O(log n) - deletes a key-value pair from this tree; returns
     * a pair (resulting-tree, was-deleted).
     */
    public BalancedBst<A> delete(A key) {
        var updatedRep = delete(key, rep);
        return new BalancedBst<>(order, updatedRep);
    }

    private AlgebraicTr<A> delete(A key, AlgebraicTr<A> t) {
        return switch (t) {
            case AlgebraicTr.Empty<A> e -> e;
            case AlgebraicTr.Node(var lvt, AlgebraicTr.Empty<A> _, var kt, var rt) -> rt;
            case AlgebraicTr.Node(var lvt, var lt, var k, AlgebraicTr.Empty<A> _) -> lt;
            case AlgebraicTr.Node(var lvt, var lt, var kt, var rt) -> {
                if (order.compare(key, kt) < 0) {
                    yield adjust(AlgebraicTr.node(lvt, delete(key, lt), kt, rt));
                } else if (order.compare(key, kt) > 0) {
                    yield adjust(AlgebraicTr.node(lvt, lt, kt, delete(key, rt)));
                } else { // found key to delete
                    // node with two children; use in-order predecessor from left subtree
                    Pair<AlgebraicTr<A>, A> p = dellrg(lt);
                    yield adjust(AlgebraicTr.node(lvt, p.first(), p.second(), rt));
                }
            }
        };
    }

    // delete regular
    private Pair<AlgebraicTr<A>, A> dellrg(AlgebraicTr<A> t) {
        return switch (t) {
            case AlgebraicTr.Empty<A> _ ->
                    throw new NoSuchElementException("Cannot find in-order predecessor in an empty tree.");
            case AlgebraicTr.Node(_, var lt, var kt, AlgebraicTr.Empty<A> _) -> Pair.of(lt, kt);
            case AlgebraicTr.Node(var lvt, var lt, var kt, var rt) -> {
                var p = dellrg(rt);
                yield Pair.of(AlgebraicTr.node(lvt, lt, kt, p.first()), p.second());
            }
        };
    }

    /**
     * Adjusts the node after deletion by possibly decreasing its level and then
     * applying skew and split operations to re-establish the AA tree invariants.
     */
    private AlgebraicTr<A> adjust(AlgebraicTr<A> t) {
        return switch (t) {
            case AlgebraicTr.Empty<A> _ -> t;
            case AlgebraicTr.Node(var curLvl, var lt, var kt, var rt) -> {
                // Compute the expected level based on the children.
                int expectedLvl = Math.min(lvl(lt), lvl(rt)) + 1;
                int newLvl = curLvl;
                var newRt = rt;
                if (expectedLvl < curLvl) {
                    newLvl = expectedLvl;
                    // If the right child is a node and its level is too high, adjust it.
                    if (rt instanceof AlgebraicTr.Node<A> rnode && lvl(rnode) > expectedLvl) {
                        newRt = AlgebraicTr.node(expectedLvl, rnode.left(), rnode.key(), rnode.right());
                    }
                }
                // Reconstruct node with new level and (possibly) updated right subtree.
                var newNode = AlgebraicTr.node(newLvl, lt, kt, newRt);
                // Apply skew on the current node.
                var skewed = skew(newNode);
                // If the right child is non-empty, skew it as well.
                var skewedRight = switch (skewed) {
                    case AlgebraicTr.Node(var lvl1, var left1, var key1, var right1)
                            when !(right1 instanceof AlgebraicTr.Empty) ->
                            AlgebraicTr.node(lvl1, left1, key1, skew(right1));
                    default -> skewed;
                };
                // apply split on the current node.
                var splitNode = split(skewedRight);
                // and if possible, split the right child.
                if (splitNode instanceof AlgebraicTr.Node(var lvl2, var left2, var key2, var right2)
                        && !(right2 instanceof AlgebraicTr.Empty)) {
                    yield AlgebraicTr.node(lvl2, left2, key2, split(right2));
                } else {
                    yield splitNode;
                }
            }
        };
    }

    private int lvl(AlgebraicTr<A> t) {
        return switch (t) {
            case AlgebraicTr.Node(var lvt, _, _, _) -> lvt;
            default                                 -> 0;
        };
    }

    private boolean sngl(AlgebraicTr<A> t) {
        return switch (t) {
            // empty trees are never a single
            case AlgebraicTr.Empty<?> _ -> false;
            // any tree with an empty right subtree always considered a single
            case AlgebraicTr.Node(_, _, _, AlgebraicTr.Empty<A> _) -> true;
            // any tree with a right subtree w/ a lower level than the root
            // is a single
            case AlgebraicTr.Node(var lvx,
                                  _,
                                  _,
                                  AlgebraicTr.Node(var lvy, _, _, _))
                    -> lvx > lvy;
        };
    }

    @Override public Iterator<A> iterator() {
        return new InOrderBstIter();
    }

    private final class InOrderBstIter implements Iterator<A> {

        /**
         * Simulates the recursive call stack to avoid recursion. Avoiding
         * recursion however is not strictly something we need to avoid here
         * (the tree being guaranteed to be balanced *I think(?)* rules out
         * deep recursive call stacks)
         */
        private final Stack<AlgebraicTr<A>> stack = new Stack<>();
        private A nextElement;

        public InOrderBstIter() {
            pushLeft(rep);
            advance(); // init nextElement
        }

        private void pushLeft(AlgebraicTr<A> node) {
            while (node instanceof AlgebraicTr.Node<A> n) {
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
                case AlgebraicTr.Node(_, _, var k, var b) -> {
                    pushLeft(b);
                    nextElement = k;
                }
                case AlgebraicTr.Empty<A> _ -> nextElement = null;
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
