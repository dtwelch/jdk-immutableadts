package org.rsrg.immutableadts;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * A linear data structure that allows fast concatenation. Ported/adapted from:
 * the flix compiler:
 * <a href="https://github.com/flix/flix/blob/master/main/src/ca/uwaterloo/flix/util/collection/Chain.scala">
 * here</a>
 * Unit tests (will be) moved as well.
 */
public sealed interface VChain<A> extends Iterable<A> {

    default VChain<A> concat(VChain<A> o) {
        if (this == Empty.EmptyInst) {
            return o;
        } else if (o == Empty.EmptyInst) {
            return this;
        } else {
            return link(this, o);
        }
    }

    final class Empty<A> implements VChain<A> {
        public static final VChain<?> EmptyInst = new Empty<>();

        private Empty() {
        }

        @Override public String toString() {
            return "Chain[]";
        }
    }

    default boolean isEmpty() {
        return switch (this) {
            case VChain.Empty<A> _ -> true;
            case VChain.Link(var l, var r) -> l.isEmpty() && r.isEmpty();
            case VChain.Proxy(var xs) -> xs._null();
        };
    }

    record Link<A>(VChain<A> l, VChain<A> r) implements VChain<A> {
        @Override public boolean equals(Object o) {
            return switch (o) {
                case VChain<?> other -> this.toList().equals(other.toList());
                default -> false;
            };
        }

        @Override public int hashCode() {
            return this.toList().hashCode();
        }

        @Override public String toString() {
            return String.format("Chain[%s]", this.mkString(", "));
        }
    }

    // I think the original scala's choice of Seq inside a proxy
    // doesn't really effect the larger big O characteristics of this
    // rope like data structure (will still support O(1) concatenations
    // list1.errors().concat(list2.errors())
    // TLDR: no need to implement a vector or something using the finger
    // tree started... (just use the immutable list in the leafs)
    record Proxy<A>(VList<A> xs) implements VChain<A> {
        @Override public boolean equals(Object o) {
            return switch (o) {
                case VChain<?> other -> this.toList().equals(other.toList());
                default -> false;
            };
        }

        @Override public int hashCode() {
            return xs.hashCode();
        }

        @Override public String toString() {
            return String.format("Chain[%s]", this.mkString(", "));
        }
    }


    default Maybe<A> head() {
        var current = this;
        while (true) {
            switch (current) {
                case Empty<A> _ -> {
                    return Maybe.none();
                }
                case Link(Empty<A> _, var r) -> current = r;
                case Link(var l, _) -> current = l;
                case Proxy(var xs) -> {
                    return xs.headMaybe();
                }
            }
        }
    }

    default int length() {
        int len = 0;
        var stack = new ArrayDeque<VChain<A>>();
        stack.push(this);

        while (!stack.isEmpty()) {
            var current = stack.pop();
            switch (current) {
                case Empty<A> _ -> {
                }
                case Link(var l, var r) -> {
                    stack.push(l);
                    stack.push(r);
                }
                case Proxy(var xs) -> len += xs.size();
            }
        }
        return len;
    }

    /**
     * Returns true only if an element in {@code this} satisfies
     * the predicate {@code f}.
     */
    default boolean exists(Function<A, Boolean> f) {
        return switch (this) {
            case Empty<A> _ -> false;
            case Link(var l, var r) -> l.exists(f) || r.exists(f);
            case Proxy(var xs) -> xs.anyMatch(f::apply);
        };
    }

    /**
     * Returns a new chain with {@code f} applied to every element
     * in {@code this}.
     */
    default <B> VChain<B> map(Function<A, B> f) {
        return switch (this) {
            case Empty<A> _ -> VChain.empty();
            case Link(var l, var r) -> VChain.link(l.map(f), r.map(f));
            case Proxy(var xs) -> VChain.proxy(xs.map(f));
        };
    }

    default VList<A> toList() {
        var buf = new java.util.ArrayList<A>(length());
        for (A x : this) {
            buf.add(x);
        }
        return VList.ofAll(buf);
    }

    default String mkString(String sep) {
        var sb = new StringBuilder();
        var first = true;
        for (var x : this) {
            if (first) {
                sb.append(x);
                first = false;
            } else {
                sb.append(sep).append(x);
            }
        }
        return sb.toString();
    }

    @Override default Iterator<A> iterator() {
        return new ChainIter<>(this);
    }

    final class ChainIter<A> implements Iterator<A> {
        private final Deque<VChain<A>> stack = new ArrayDeque<>();
        private Iterator<A> proxyIt = null;

        public ChainIter(VChain<A> root) {
            stack.push(root);
        }

        @Override public boolean hasNext() {
            if (proxyIt != null && proxyIt.hasNext()) {
                return true;
            }
            proxyIt = null;

            while (!stack.isEmpty()) {
                var cur = stack.pop();
                switch (cur) {
                    case Empty<A> _ -> {
                    }
                    case Link(var l, var r) -> {
                        stack.push(r);
                        stack.push(l);
                    }
                    case Proxy(var xs) -> {
                        proxyIt = xs.iterator();
                        if (proxyIt.hasNext()) {
                            return true;
                        } else {
                            proxyIt = null;
                        }
                    }
                }
            }
            return false;
        }

        @Override public A next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // by contract: proxyIt is not null and has a next element
            return proxyIt.next();
        }
    }

    // static factory methods

    static <A> VChain<A> of(A... xs) {
        if (xs.length == 0) {
            return VChain.empty();
        }
        return proxy(VList.ofAll(java.util.Arrays.asList(xs)));
    }

    static <A> VChain<A> from(Iterable<A> items) {
        for (A ignored : items) {
            return proxy(VList.ofAll(items));
        }
        return VChain.empty();
    }

    static <A> VChain<A> proxy(A x) {
        return proxy(VList.of(x));
    }

    static <A> VChain<A> proxy(VList<A> list) {
        return new VChain.Proxy<>(list);
    }

    static <A> VChain<A> link(VChain<A> l, VChain<A> r) {
        return new VChain.Link<>(l, r);
    }

    @SuppressWarnings("unchecked") static <A> VChain<A> empty() {
        return (VChain<A>) Empty.EmptyInst;
    }
}
