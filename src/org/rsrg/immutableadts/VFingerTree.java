package org.rsrg.immutableadts;

import org.rsrg.immutableadts.util.Pair;

public interface VFingerTree {

    // todo (maybe)
    //      https://andrew.gibiansky.com/blog/haskell/finger-trees/

    // nonempty 2-3 trees with all items stored within the leafs
    // + some cached annotation of type A
    sealed interface Node<E, A> {
        record Tip<E, A>(E entry, A ann) implements Node<E, A> {}

        record Node2<E, A>(A ann, Node<E, A> s1, Node<E, A> s2) implements Node<E, A> {}

        record Node3<E, A>(A ann, Node<E, A> s1, Node<E, A> s2, Node<E, A> s3) implements Node<E, A> {}
    }

    // one to four ordered nodes
    sealed interface Digit<E, A> {
        record One<E, A>(Node<E, A> s1) implements Digit<E, A> {}

        record Two<E, A>(Node<E, A> s1, Node<E, A> s2) implements Digit<E, A> {}

        record Three<E, A>(Node<E, A> s1, Node<E, A> s2, Node<E, A> s3) implements Digit<E, A> {}

        record Four<E, A>(Node<E, A> s1, Node<E, A> s2, Node<E, A> s3, Node<E, A> s4) implements Digit<E, A> {}
    }

    sealed interface FingerTreeStruc<E, A> {
        final class Empty<E, A> implements FingerTreeStruc<E, A> {
            public static final Empty<?, ?> Instance = new Empty<>();

            private Empty() {
            }
        }

        @SuppressWarnings("unchecked") static <E1, A1> FingerTreeStruc<E1, A1> empty() {
            return (FingerTreeStruc<E1, A1>) Empty.Instance;
        }

        record Single<E, A>(Node<E, A> ann) implements FingerTreeStruc<E, A> {}

        static <E1, A1> FingerTreeStruc<E1, A1> single(Node<E1, A1> node) {
            return new Single<>(node);
        }

        record Deep<E, A>(A ann, // measure for whole subtree
                          Digit<E, A> leftNodeGroup, FingerTreeStruc<E, A> finger,
                          Digit<E, A> rightNodeGroup) implements FingerTreeStruc<E, A> {}
    }

    interface Measurable<E1, A1> {
        A1 identity();

        A1 combine(A1 left, A1 right);

        A1 measureOf(E1 elem);
    }

    // gmn = get measure (of) node
    // this method just returns the cached measure of the node
    // in this (hinze paterson) finger tree -- m is not actually
    // needed (as we don't have to recalc);
    //
    // so m is here for consistency with other functions that
    // do rely on the ability to recompute a node's measure
    static <E, A> A gmn(Node<E, A> n, Measurable<E, A> m) {
        return switch (n) {
            case Node.Tip(var _, var a) -> a;
            case Node.Node2(var a, _, _) -> a;
            case Node.Node3(var a, _, _, _) -> a;
        };
    }

    // gmd = get measure of digit(s)
    // so this function computes the sum of the annotation measures
    // of each Node stored in digit d
    static <E, A> A gmd(Digit<E, A> digit, Measurable<E, A> m) {
        return switch (digit) {
            case Digit.One(var a) ->
                    gmn(a, m);
            case Digit.Two(var a, var b) ->
                    m.combine(gmn(a, m), gmn(b, m));
            case Digit.Three(var a, var b, var c) ->
                    m.combine(m.combine(gmn(a, m), gmn(b, m)), gmn(c, m));
            case Digit.Four(var a, var b, var c, var d) ->
                    m.combine(m.combine(m.combine(gmn(a, m), gmn(b, m)),
                            gmn(c, m)), gmn(d, m));
        };
    }

    // read out the cached annotation of a finger tree
    static <E, A> A gmft(FingerTreeStruc<E, A> tree, Measurable<E, A> m) {
        return switch (tree) {
            case FingerTreeStruc.Empty<E, A> _          -> m.identity();
            case FingerTreeStruc.Single(var nd)         -> gmn(nd, m);
            case FingerTreeStruc.Deep(var a, _, _, _)   -> a;
        };
    }

    static <E, A> VList<Pair<E, A>> nodeToList(Node<E, A> n) {
        return switch (n) {
            case Node.Tip(var e, var a)                 -> VList.of(Pair.of(e, a));
            case Node.Node2(var _, var a, var b)        -> nodeToList(a).append(nodeToList(b));
            case Node.Node3(var _, var a, var b, var c) -> nodeToList(a).append(nodeToList(b)).append(nodeToList(c));
        };
    }

    static <E, A> VList<Pair<E, A>> digitToList(Digit<E, A> digit) {
        return switch (digit) {
            case Digit.One(var a) -> nodeToList(a);
            case Digit.Two(var a, var b) -> nodeToList(a).append(nodeToList(b));
            case Digit.Three(var a, var b, var c) ->
                    nodeToList(a).append(nodeToList(b)).append(nodeToList(c));
            case Digit.Four(var a, var b, var c, var d) ->
                    nodeToList(a).append(nodeToList(b)).append(nodeToList(c)).append(nodeToList(d));
        };
    }

    // append a node at the left end
    static <E, A> FingerTreeStruc<E, A> nlcons(
            Node<E, A> node, FingerTreeStruc<E, A> tree, Measurable<E, A> m) {
        // recursively append node -- if the digit is full push down a node3
        return switch (Pair.of(node, tree)) {
            case Pair(var a, FingerTreeStruc.Empty<E, A> _) -> FingerTreeStruc.single(a);
            default -> null;
        };
    }
}
