package org.rsrg.immutableadts;

import java.util.Comparator;
import java.util.Iterator;

public final class VTreeSet<A> implements Iterable<A> {

    private final int size;
    private final BalancedBst<A> bst;
    private final Comparator<A> keyOrder;

    private VTreeSet(Comparator<A> keyOrder, BalancedBst<A> bst, int size) {
        this.keyOrder = keyOrder;
        this.bst = bst;
        this.size = size;
    }

    public static <T> VTreeSet<T> empty(Comparator<T> o) {
        return new VTreeSet<>(o, BalancedBst.empty(o), 0);
    }

    public static <A extends Comparable<A>> VTreeSet<A> empty() {
        return empty(Comparable::compareTo);
    }

    public static <T> VTreeSet<T> singleton(Comparator<T> o, T item) {
        return empty(o).insert(item);
    }

    public static <T extends Comparable<T>> VTreeSet<T> singleton(T item) {
        return singleton(Comparable::compareTo, item);
    }

    /**
     * O(log n) - adds {@code item} to this set; ignores it if already
     * present.
     */
    public VTreeSet<A> insert(A item) {
        if (!bst.contains(item)) {
            return this;
        }
        var updatedBst = this.bst.insert(item);
        return new VTreeSet<>(keyOrder, updatedBst, this.size + 1);
    }

    /** O(1) - returns the number of items in this set. */
    public int size() {
        return size;
    }

    /** O(log n) - removes {@code item} from this set if present. */
    public VTreeSet<A> remove(A item) {
        var updatedSize = this.size;
        var updatedBst = bst;
        if (bst.contains(item)) {
            updatedBst = bst.delete(item);
            updatedSize = updatedSize - 1;
        }
        return new VTreeSet<>(keyOrder, updatedBst, updatedSize);
    }

    /**
     * (note) potential: O((n+m) * log(n+m)) - returns the union of sets
     * {@code s} and {@code t}.
     */
    public VTreeSet<A> union(VTreeSet<A> s, VTreeSet<A> t) {
        var combined = VTreeSet.empty(keyOrder);
        for (var a : s) { // O(n log |s|) - or: O(n log n)
            combined = combined.insert(a);
        }
        for (var a : t) { // O(m log |t| - |s|): or: O(m log (n + m))
            combined = combined.insert(a);
        }
        return combined;
    }

    @Override public Iterator<A> iterator() {
        return bst.iterator();
    }

    //public VTreeSet<A> foldl()

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
