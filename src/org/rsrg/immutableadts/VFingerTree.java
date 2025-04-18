package org.rsrg.immutableadts;

import static org.rsrg.immutableadts.VFingerTree.FingerTreeStruc.*;

public final class VFingerTree {

    private VFingerTree() {}
    // todo (maybe)
    //      https://andrew.gibiansky.com/blog/haskell/finger-trees/
    // hmmm: this seems like a better fit for an implementation of an immutable Vector type:
    // https://infoscience.epfl.ch/server/api/core/bitstreams/e5d662ea-1e8d-4dda-b917-8cbb8bb40bf9/content

    // nonempty 2-3 trees with all items stored within the leafs
    // + some cached annotation of type A
    sealed interface Node<E, A> {
        record Tip<E, A>(E entry, A ann) implements Node<E, A> {}

        record Node2<E, A>(A ann, Node<E, A> s1, Node<E, A> s2) implements Node<E, A> {}

        static <E1, A1> Node2<E1, A1> node2(Node<E1, A1> nd1, Node<E1, A1> nd2, Measurable<E1, A1> m0) {
            return new Node2<>(m0.combine(gmn(nd1), gmn(nd2)), nd1, nd2);
        }

        record Node3<E, A>(A ann, Node<E, A> s1, Node<E, A> s2, Node<E, A> s3) implements Node<E, A> {}
        static <E1, A1> Node3<E1, A1> node3(Node<E1, A1> nd1, Node<E1, A1> nd2, Node<E1, A1> nd3, Measurable<E1, A1> m0) {
            return new Node3<>(m0.combine(m0.combine(gmn(nd1), gmn(nd2)), gmn(nd3)), nd1, nd2, nd3);
        }
    }

    // one to four ordered nodes
    sealed interface Digit<E, A> {
        record One<E, A>(Node<E, A> s1) implements Digit<E, A> {}
        static <E1, A1> Digit<E1, A1> one(Node<E1, A1> e) {
            return new One<>(e);
        }
        record Two<E, A>(Node<E, A> s1, Node<E, A> s2) implements Digit<E, A> {}

        static <E1, A1> Digit<E1, A1> two(Node<E1, A1> e1, Node<E1, A1> e2) {
            return new Two<>(e1, e2);
        }
        record Three<E, A>(Node<E, A> s1, Node<E, A> s2, Node<E, A> s3) implements Digit<E, A> {}

        static <E1, A1> Digit<E1, A1> three(Node<E1, A1> e1, Node<E1, A1> e2, Node<E1, A1> e3) {
            return new Three<>(e1, e2, e3);
        }
        record Four<E, A>(Node<E, A> s1, Node<E, A> s2, Node<E, A> s3, Node<E, A> s4) implements Digit<E, A> {}

        static <E1, A1> Digit<E1, A1> four(Node<E1, A1> e1, Node<E1, A1> e2, Node<E1, A1> e3, Node<E1, A1> e4) {
            return new Four<>(e1, e2, e3, e4);
        }
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

        static <E1, A1> FingerTreeStruc<E1, A1> deep(
                Digit<E1, A1> prefixDigit, FingerTreeStruc<E1, A1> middleTree,
                Digit<E1, A1> suffixDigit, Measurable<E1, A1> m) {
            return new Deep<>(m.combine(m.combine(gmd(prefixDigit, m),
                    gmft(middleTree, m)), gmd(suffixDigit, m)),
                    prefixDigit, middleTree, suffixDigit);
        }
    }

    interface Measurable<E1, A1> {
        A1 identity();

        A1 combine(A1 left, A1 right);

        A1 measureOf(E1 elem);
    }

    // gmn = get measure (of) node
    // returns the cached measure of the node in a finger tree 
    static <E, A> A gmn(Node<E, A> n) {
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
            case Digit.One(var a) -> gmn(a);
            case Digit.Two(var a, var b) ->
                    m.combine(gmn(a), gmn(b));
            case Digit.Three(var a, var b, var c) ->
                    m.combine(m.combine(gmn(a), gmn(b)), gmn(c));
            case Digit.Four(var a, var b, var c, var d) ->
                    m.combine(m.combine(m.combine(
                            gmn(a), gmn(b)), gmn(c)), gmn(d));
        };
    }

    // read out the cached annotation of a finger tree
    static <E, A> A gmft(FingerTreeStruc<E, A> tree, Measurable<E, A> m) {
        return switch (tree) {
            case FingerTreeStruc.Empty<E, A> _  -> m.identity();
            case Single(var nd)                 -> gmn(nd);
            case Deep(var a, _, _, _)           -> a;
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

    /**
     * O(n) avg, O(n^2) worst; returns a list representation of a finger tree.
     * Worst case requires it seems some pretty pathological insertion patterns...
     */
    static <E, A> VList<Pair<E, A>> toList(FingerTreeStruc<E, A> tree) {
        return switch (tree) {
            case Empty<E, A> _ -> VList.empty();
            case Single(var a) -> nodeToList(a);
            // NB pf=prefix digit; sf=suffix digit
            case Deep(_, var pr, var m, var sf) -> digitToList(pr).append(toList(m)).append(digitToList(sf));
        };
    }

    // appending

    // append a node at the left end
    static <E, A> FingerTreeStruc<E, A> nlcons(
            Node<E, A> node, FingerTreeStruc<E, A> tree, Measurable<E, A> m0) {
        // recursively append node -- if the digit is full push down a node3
        return switch (Pair.of(node, tree)) {
            // pf=prefix digit; sf=suffix digit
            case Pair(var a, FingerTreeStruc.Empty<E, A> _) -> single(a);
            case Pair(var a, FingerTreeStruc.Single(var b)) ->
                    deep(Digit.one(a), empty(), Digit.one(b), m0);
            case Pair(var a, FingerTreeStruc.Deep(_, Digit.One(var b), var m, var sf)) ->
                    FingerTreeStruc.deep(Digit.two(a, b), m, sf, m0);
            case Pair(var a, FingerTreeStruc.Deep(_, Digit.Two(var b, var c), var m, var sf)) ->
                    FingerTreeStruc.deep(Digit.three(a, b, c), m, sf, m0);
            case Pair(var a, FingerTreeStruc.Deep(_, Digit.Three(var b, var c, var d), var m, var sf)) ->
                    FingerTreeStruc.deep(Digit.four(a, b, c, d), m, sf, m0);
            case Pair(var a, FingerTreeStruc.Deep(_, Digit.Four(var b, var c, var d, var e), var m, var sf)) ->
                    FingerTreeStruc.deep(Digit.two(a, b), nlcons(Node.node3(c, d, e, m0), m, m0), sf, m0);
        };
    }
}
