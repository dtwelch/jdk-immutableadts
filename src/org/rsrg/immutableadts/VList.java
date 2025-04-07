package org.rsrg.immutableadts;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public final class VList<A> implements Iterable<A> {

    private final AlgebraicLst<A> lst;
    private final int size;

    private VList(AlgebraicLst<A> lst, int size) {
        this.lst = lst;
        this.size = size;
    }

    public static <T> VList<T> empty() {
        return new VList<>(AlgebraicLst.empty(), 0);
    }

    @SafeVarargs public static <T> VList<T> of(T... ts) {
        var res = AlgebraicLst.<T>empty();
        for (int i = ts.length - 1; i >= 0; i--) {
            res = AlgebraicLst.cons(ts[i], res);
        }
        return new VList<>(res, ts.length);
    }

    public static <T> VList<T> ofAll(Iterable<T> items) {
        var buffer = new ArrayList<T>();
        for (var item : items) {
            buffer.add(item);
        }

        // for efficient consing
        var lst = AlgebraicLst.<T>empty();
        for (int i = buffer.size() - 1; i >= 0; i--) {
            lst = AlgebraicLst.cons(buffer.get(i), lst);
        }
        return new VList<>(lst, buffer.size());
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

    /** O(1) - returns true only if this list is empty; false otherwise. */
    public boolean _null() { return size == 0; }

    /**
     * O(1) - retrieves the head of this list;
     * throws an {@link IllegalStateException} if called on an empty list.
     */
    public A head() {
        return switch (lst) {
            case AlgebraicLst.NonEmpty(var x, _) -> x;
            case AlgebraicLst.Empty<A> _ ->
                    throw new IllegalStateException("head called on empty list");
        };
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

    /**
     * O(n) - folds the list into a single value of type {@code B} from
     * left to right according to function {@code f} (a -> b -> c) -- which
     * carries an accumulator (stack safe)
     */
    public <B> B foldLeft(B initial, BiFunction<B, A, B> f) {
        B acc = initial;
        var current = lst;
        while (!current._null()) {
            acc = f.apply(acc, current.head());
            current = current.tail();
        }
        return acc;
    }

    public boolean anyMatch(Predicate<? super A> predicate) {
        for (A element : this) {
            if (predicate.test(element)) {
                return true;
            }
        }
        return false;
    }

    public <B> B foldRight(B initial, BiFunction<A, B, B> f) {
        var stack = new ArrayDeque<A>();
        var current = lst;
        while (!current._null()) {
            stack.push(current.head());
            current = current.tail();
        }
        B acc = initial;
        while (!stack.isEmpty()) {
            acc = f.apply(stack.pop(), acc);
        }
        return acc;
    }

    public <B> VList<B> map(Function<A, B> f) {
        return new VList<>(foldRight(AlgebraicLst.empty(),
                (x, acc) -> AlgebraicLst.cons(f.apply(x), acc)), size);
    }

    @Override public Iterator<A> iterator() {
        return new Iterator<>() {
            private AlgebraicLst<A> cur = lst;

            @Override public boolean hasNext() {
                return !cur._null();
            }

            @Override public A next() {
                if (cur._null()) {
                    throw new NoSuchElementException();
                }
                A elem = cur.head();
                cur = cur.tail();
                return elem;
            }
        };
    }


    public String mkString(String delim)  {
        return mkString("", delim, "");
    }

    public String mkString(String left, String delim, String right) {
        var sb = new StringBuilder(left);
        var first = true;
        for (var node : this) {
           if (first) {
               sb.append(node);
               first = false;
           } else {
               sb.append(delim).append(node);
           }
        }
        return sb.append(right).toString();
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
