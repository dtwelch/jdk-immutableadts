package org.rsrg.immutableadts;

import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;
import org.rsrg.immutableadts.util.Pair;

public final class FingerTreePropertyTests {

    private static final VFingerTree.Measurable<Integer, Integer> SumMeasure =
            new VFingerTree.Measurable<>() {
                @Override public Integer identity() {
                    return 0;
                }

                @Override public Integer combine(Integer left, Integer right) {
                    return left + right;
                }

                @Override public Integer measureOf(Integer elem) {
                    return elem;
                }
            };

    /**
     * Generate random lists of (E, A) pairs, where E and A are both integers;
     * each pair is {@code Pair(E,A)}
     */
    @Provide Arbitrary<VList<Pair<Integer, Integer>>> pairsOfIntInt() {
        return Arbitraries.integers()
                .tuple2()
                .map(tuple -> Pair.of(tuple.get1(), tuple.get2()))
                .list()
                .ofMinSize(0).ofMaxSize(50)
                .map(VList::ofAll);
    }


    /**
     * 1) round-trip property:
     * ∀ (list of (E,A)) L, toList(toTree(L)) = L
     */
    @Property void roundTripProperty(
            @ForAll("pairsOfIntInt") VList<Pair<Integer, Integer>> input) {
        var ft = buildFingerTreeFromList(input);

        // convert back to list
        var output = fingerTreeToList(ft);

        // check equality
        Assertions.assertEquals(input, output,
                "Round-trip failed: toList(toTree(L)) != L");
    }

    private VFingerTree.FingerTreeStruc<Integer,Integer> buildFingerTreeFromList(
            VList<Pair<Integer,Integer>> input) {
        throw new UnsupportedOperationException("not done");
    }

    private VList<Pair<Integer, Integer>> fingerTreeToList(VFingerTree.FingerTreeStruc<Integer, Integer> ft) {
        throw new UnsupportedOperationException("not done");
    }
}