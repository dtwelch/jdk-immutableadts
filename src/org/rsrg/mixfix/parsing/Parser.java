package org.rsrg.mixfix.parsing;

import java.util.function.Function;

public sealed interface Parser<A> {
    final class Fail<A>                                     implements Parser<A> {
        static final Parser<?> Instance = new Fail<>();
        private Fail() {}
    }
    record Or<A>(Parser<A> a, Parser<A> b)                  implements Parser<A> {}
    record App<A, B>(Parser<Function<A, B>> f, Parser<A> x) implements  Parser<A> {}
}
