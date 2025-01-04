package org.rsrg.mixfix.immutableadts;

import org.rsrg.mixfix.util.Pair;

public final class VList<A> {

    private final AlgebraicLst<A> lst;
    private final int size;

    private VList(AlgebraicLst<A> lst, int size) {
        this.lst = lst;
        this.size = size;
    }

    public static <T> VList<T> of(T... ts) {
        var res = AlgebraicLst.<T>empty();
        for (int i = ts.length - 1; i >= 0; i--) {
            res = AlgebraicLst.cons(ts[i], res);
        }
        return new VList<>(res, ts.length);
    }

    public static <T> VList<T> empty() {
        return new VList<>(AlgebraicLst.empty(), 0);
    }

    /**
     * O(n) -- appends all items from ys (in order) to this
     * the end of this list.
     */
    public VList<A> append(VList<A> ys) {
        var newLst = append(lst, ys.lst);
        return new VList<>(newLst, size + ys.size);
    }

    private AlgebraicLst<A> append(AlgebraicLst<A> xs,
                                   AlgebraicLst<A> ys) {
        return switch (Pair.of(xs, ys)) {
            case Pair(AlgebraicLst.Empty<A> _, var b) -> b;
            case Pair(AlgebraicLst.NonEmpty(var head, var tail), var ys0) ->
                    AlgebraicLst.cons(head, append(tail, ys0));
        };
    }

    /** O(1) - returns the number of items in this list. */
    public int size() {
        return size;
    }

    @Override public boolean equals(Object o) {
        return switch (o) {
            case VList<?> other ->
                    this.size == other.size && this.lst.equals(other.lst);
            default -> false;
        };
    }

    @Override public int hashCode() {
        return 31 * lst.hashCode() + size;
    }
}
