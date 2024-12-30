package org.rsrg.mixfix.immutableadts;

import org.rsrg.mixfix.util.Maybe;
import org.rsrg.mixfix.util.Pair;

import java.util.Comparator;

public final class VTreeMap<K, V> {
    private final int size;
    private final BalancedBst<Pair<K, V>> bst;
    private final Comparator<K> keyOrder;

    private VTreeMap(Comparator<K> keyOrder, BalancedBst<Pair<K, V>> bst, int size) {
        this.keyOrder = keyOrder;
        this.bst = bst;
        this.size = size;
    }

    public static <A, B> VTreeMap<A, B> empty(Comparator<A> o) {
        //@formatter:off
        return new VTreeMap<>(o,
                BalancedBst.empty((p1, p2) -> o.compare(p1.first(), p2.first())),
                0);
        //@formatter:on
    }

    public static <A extends Comparable<A>, B extends Comparable<B>> VTreeMap<A, B> empty() {
        return empty(Comparable::compareTo);
    }

    public VTreeMap<K, V> insert(K key, V val) {
        var kv = Pair.of(key, val);
        var sz = this.size;
        throw new UnsupportedOperationException("not done");
        /*var existingPair = bst.find(kv);
        return switch (existingPair) {
            case Maybe.Some(Pair(var k, var v)) -> bst
            default -> new VTreeMap<>(keyOrder, , sz + 1);
        };
        return*/
    }


}
