package org.rsrg.immutableadts;

import org.rsrg.immutableadts.util.Maybe;

import java.util.ArrayDeque;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A linear data structure that allows fast concatenation. Ported/adapted from:
 * the flix compiler:
 * <a href="https://github.com/flix/flix/blob/master/main/src/ca/uwaterloo/flix/util/collection/Chain.scala">
 *     here</a>
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
    }

    @SuppressWarnings("unchecked") static <A> VChain<A> empty() {
        return (VChain<A>) Empty.EmptyInst;
    }

    default boolean isEmpty() {
        return switch (this) {
            case VChain.Empty<A> _          -> true;
            case VChain.Link(var l, var r)  -> l.isEmpty() && r.isEmpty();
            case VChain.Proxy(var xs)       -> xs._null();
        };
    }

    record Link<A>(VChain<A> l, VChain<A> r) implements VChain<A> {}

    static <A> VChain<A> link(VChain<A> l, VChain<A> r) {
        return new VChain.Link<>(l, r);
    }
    // I think the original scala's choice of Seq inside a proxy
    // doesn't really effect the larger big O characteristics of this
    // rope like data structure (will still support O(1) concatenations
    // list1.errors().concat(list2.errors())
    // TLDR: no need to implement a vector or something using the finger
    // tree started... (just use the immutable list in the leafs)
    record Proxy<A>(VList<A> xs) implements VChain<A> {}

    static <A> VChain<A> proxy(A x) {
        return proxy(VList.of(x));
    }

    static <A> VChain<A> proxy(VList<A> list) {
        return new VChain.Proxy<>(list);
    }

    default Maybe<A> head() {
        var current = this;
        while (true) {
            switch (current) {
                case Empty<A> _ -> {
                    return Maybe.none();
                }
                case Link(Empty<A> _, var r) -> current = r;
                case Link(var l, _)          -> current = l;
                case Proxy(var xs)           -> {
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
                case Empty<A> _          -> {}
                case Link(var l, var r)  -> {
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
            case Empty<A> _         -> false;
            case Link(var l, var r) -> l.exists(f) || r.exists(f);
            case Proxy(var xs)      -> xs.anyMatch(f::apply);
        };
    }

    /**
     * Returns a new chain with {@code f} applied to every element
     * in {@code this}.
     */
    default <B> VChain<B> map(Function<A, B> f) {
        return switch (this) {
            case Empty<A> _         -> VChain.empty();
            case Link(var l, var r) -> VChain.link(l.map(f), r.map(f));
            case Proxy(var xs)      -> VChain.proxy(xs.map(f));
        };
    }

    @Override default void forEach(Consumer<? super A> f) {
        switch (this) {
            case Empty<A> _ -> {}
            case Link<A> c -> {
                c.l.forEach(f);
                c.r.forEach(f);
            }
            case Proxy<A> c -> c.xs.forEach(f);
        }
    }
}
