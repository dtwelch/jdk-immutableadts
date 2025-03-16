package org.rsrg.immutableadts;

import org.rsrg.immutableadts.util.Pair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class VList<A> implements Iterable<A> {

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
     * the end of this list. Should be stack safe (avoids recursion)
     */
    public VList<A> append(VList<A> ys) {
        var newLst = append(lst, ys.lst);
        return new VList<>(newLst, size + ys.size);
    }

    private AlgebraicLst<A> append(AlgebraicLst<A> xs, AlgebraicLst<A> ys) {
        if (xs._null()) {
            return ys;
        }
        var buffer = new ArrayList<A>();
        while (!xs._null()) {
            buffer.add(xs.head());
            xs = xs.tail();
        }
        var result = ys;
        for (int i = buffer.size() - 1; i >= 0; i--) {
            result = AlgebraicLst.cons(buffer.get(i), result);
        }
        return result;
    }

    /** O(1) - prepends (cons) an {@code element} onto the front of this list. */
    public VList<A> prepend(A element) {
        return new VList<>(AlgebraicLst.cons(element, lst), size + 1);
    }

    /** O(1) - returns the number of items in this list. */
    public int size() {
        return size;
    }

    /** O(n) - returns a new VList with the elements in reverse order. */
    public VList<A> reverse() {
        if (size <= 1) {
            return this;
        }
        AlgebraicLst<A> reversed = AlgebraicLst.empty();
        var current = lst;
        while (!current._null()) {
            var head = current.head();
            current = current.tail();
            reversed = AlgebraicLst.cons(head, reversed);
        }
        return new VList<>(reversed, size);
    }

    @Override public Iterator<A> iterator() {
        return new Iterator<>() {
            private AlgebraicLst<A> cur = lst;

            @Override
            public boolean hasNext() {
                return !cur._null();
            }

            @Override
            public A next() {
                if (cur._null()) {
                    throw new NoSuchElementException();
                }
                A elem = cur.head();
                cur = cur.tail();
                return elem;
            }
        };
    }

    @Override public boolean equals(Object o) {
        return switch (o) {
            case VList<?> other -> this.size == other.size && this.lst.equals(other.lst);
            default -> false;
        };
    }

    @Override public int hashCode() {
        return 31 * lst.hashCode() + size;
    }
}
