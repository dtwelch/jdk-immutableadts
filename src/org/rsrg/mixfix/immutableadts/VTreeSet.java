package org.rsrg.mixfix.immutableadts;

import java.util.Comparator;

public final class VTreeSet<A> {

    private final int size;
    private final BalancedBst<A> bst;
    private final Comparator<A> keyOrder;

    private VTreeSet(Comparator<A> keyOrder,
                     BalancedBst<A> bst, int size) {
        this.keyOrder = keyOrder;
        this.bst = bst;
        this.size = size;
    }

    public static <T> VTreeSet<T> empty(Comparator<T> o) {
        return new VTreeSet<>(o, BalancedBst.empty(o), 0);
    }
/*
    public static <A extends Comparable<A>, B extends Comparable<B>> VTreeMap<A, B> empty() {
        return empty(Comparable::compareTo);
    }

    private final BalancedBst<A>
    private VTreeSet()*/
}
