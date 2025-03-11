package org.rsrg.mixfix.immutableadts;

import org.rsrg.mixfix.util.Maybe;
import org.rsrg.mixfix.util.Pair;

import java.util.Comparator;

import static org.rsrg.mixfix.util.Maybe.*;

public final class VTreeMap<K, V> {

    private final int size;
    private final BalancedBst<Pair<K, V>> bst;
    private final Comparator<K> keyOrder;

    private VTreeMap(Comparator<K> keyOrder, BalancedBst<Pair<K, V>> bst,
                     int size) {
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

    /**
     * O(log n) - return true if {@code key} is present in this map;
     * false otherwise.
     */
    public Maybe<V> lookup(K key) {
        // search on a pair with a dummy (null) value -- this
        // works since our bst only compares on keys
        return switch (bst.find(Pair.of(key, null))) {
            case Some(Pair(_, var v)) -> of(v);
            default -> none();
        };
    }

    /**
     * O(log n) - returns true only if {@code key} is in this map;
     * false otherwise.
     */
    public boolean member(K key) {
        return switch (bst.find(Pair.of(key, null))) {
            case Some(Pair(var k, _)) when keyOrder.compare(k, key) == 0 ->
                    true;
            default -> false;
        };
    }

    public VList<Pair<K, V>> toList() {
        var res = VList.<Pair<K, V>>empty();
        for (var kv : bst) {
            res = res.prepend(kv);
        }
        return res;
    }

    /** O(1) - returns the number of key value pairs in this map. */
    public int size() {
        return size;
    }

    @Override public String toString() {
        var sb = new StringBuilder("[");
        var first = true;
        for (var x : bst) {
            if (first) {
                sb.append(x);
                first = false;
            } else {
                sb.append(", ").append(x);
            }
        }
        return sb.append("]").toString();
    }
}
