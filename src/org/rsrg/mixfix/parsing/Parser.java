package org.rsrg.mixfix.parsing;

import org.rsrg.mixfix.util.Maybe;
import org.rsrg.mixfix.util.Pair;
import org.rsrg.mixfix.util.Utils;

import java.util.function.Function;

import static org.rsrg.mixfix.util.Utils.*;

public sealed interface Parser<A> {
    final class Fail<A>                                     implements Parser<A> {
        static final Parser<?> Instance = new Fail<>();
        private Fail() {}
    }
    record Or<A>(Parser<A> a, Parser<A> b)                  implements Parser<A> {}
    record App<A, B>(Parser<Function<A, B>> f, Parser<A> x) implements  Parser<A> {}

    static <T> Maybe<T> runParser(String input, Parser<T> p) {
        var trimmed = dropSpaces(input);
        var result  = go(input, p);
        return switch (result) {
            case Maybe.Some(Pair(var a, var b)) when isEof(a) -> Maybe.of(b);
            default -> Maybe.none();
        };
    }

    private static <T> Maybe<Pair<String, T>> go(String input, Parser<T> p) {
        throw new UnsupportedOperationException("not done");
        /*return switch (p) {

        };*/
    }
}
