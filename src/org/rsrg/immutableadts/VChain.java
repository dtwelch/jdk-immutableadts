package org.rsrg.immutableadts;

/**
 * A linear data structure that allows fast concatenation. Ported/adapted from:
 * the flix compiler:
 * <a href="https://github.com/flix/flix/blob/master/main/src/ca/uwaterloo/flix/util/collection/Chain.scala">
 *     here</a>
 * Unit tests (will be) moved as well.
 */
public sealed interface VChain<A> {

    default VChain<A> concat(VChain<A> o) {
        if (this == Empty.EmptyInst) {
            return o;
        } else if (o == Empty.EmptyInst) {
            return this;
        } else {
            return null;
        }
    }

    final class Empty<A> implements VChain<A> {
        public static final VChain<?> EmptyInst = new Empty<>();

        private Empty() {
        }
    }

    record Link<A>(VChain<A> l, VChain<A> r) implements VChain<A> {}

    // I think the original scala's choice of Seq inside a proxy
    // doesn't really effect the larger big o characteristics of this
    // rope like data structure (will still support O(1) concatenations
    // list1.errors().concat(list2.errors())
    // TLDR: no need to implement a vector or something using the finger
    // tree started... (just the list in the leafs)
    record Proxy<A>(VList<A> xs) implements VChain<A> {}
}
