package org.rsrg.mixfix.immutableadts;

import org.rsrg.mixfix.util.Maybe;
import org.rsrg.mixfix.util.Pair;

import java.util.Comparator;

import static org.rsrg.mixfix.util.Maybe.*;

public final class VTreeMap<K, V> {

    private final int size;
    private final BalancedBst<Pair<K, V>> bst;
    private final Comparator<K> keyOrder;

    private VTreeMap(Comparator<K> keyOrder,
                     BalancedBst<Pair<K, V>> bst, int size) {
        this.keyOrder = keyOrder;
        this.bst = bst;
        this.size = size;
    }

    public static <A, B> VTreeMap<A, B> empty(Comparator<A> o) {
        //@formatter:off
        return new VTreeMap<>(o,
                BalancedBst.empty((p1, p2) ->
                        o.compare(p1.first(), p2.first())),
                0);
        //@formatter:on
    }

    public static <A extends Comparable<A>, B> VTreeMap<A, B> empty() {
        return empty(Comparable::compareTo);
    }

    public VTreeMap<K, V> insert(K key, V val) {
        var toAdd = Pair.of(key, val);
        var updatedRep = bst;
        var updatedSz = this.size;
        var maybeFound = bst.find(toAdd);
        switch (maybeFound) {
            case Some(var p) ->
                // delete found (k,v) and replace it with (k,v') where
                // v, v' may vary
                    updatedRep = bst.delete(p).insert(toAdd);
                // don't update this.size as we're just replacing (k,v) with
                // (k,v')
            default -> {
                updatedRep = bst.insert(toAdd);
                updatedSz += 1;
            }
        }
        return new VTreeMap<>(keyOrder, updatedRep, updatedSz);
    }

    public Maybe<V> get(K key) {
        // search on a pair with a dummy (null) value -- this
        // works since our bst only compares on keys
        return switch (bst.find(Pair.of(key, null))) {
            case Some(Pair(_, var v)) -> of(v);
            default                   -> none();
        };
    }

    public boolean containsKey(K key) {
        return switch (bst.find(Pair.of(key, null))) {
            case Some(Pair(var k, _)) when keyOrder.compare(k, key) == 0 -> true;
            default                                                      -> false;
        };
    }

    public int size() {
        return size;
    }


}
