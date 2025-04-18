package org.rsrg.immutableadts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public final class VChainTests {

    @Test void testToList01() {
        Assertions.assertEquals(VList.<Integer>empty(), VChain.<Integer>empty().toList());
    }

    @Test void testToList02() {
        Assertions.assertEquals(VList.of(1, 2, 3, 4), VChain.of(1, 2, 3, 4).toList());
    }

    @Test void testToList03() {
        var chain = VChain.of(1, 2).concat(VChain.of(3, 4));
        Assertions.assertEquals(VList.of(1, 2, 3, 4), chain.toList());
    }

    @Test void testToList04() {
        var chain = VChain.<Integer>empty().concat(VChain.of(1, 2, 3, 4));
        Assertions.assertEquals(VList.of(1, 2, 3, 4), chain.toList());
    }

    @Test void testToList05() {
        var chain = VChain.of(1, 2, 3, 4).concat(VChain.empty());
        Assertions.assertEquals(VList.of(1, 2, 3, 4), chain.toList());
    }

    @Test void testToList06() {
        var chains = VList.of(VChain.<Integer>empty(), VChain.of(1, 2), VChain.<Integer>empty().concat(VChain.of(3, 4)));
        var folded = chains.foldLeft(VChain.<Integer>empty(), VChain::concat);
        Assertions.assertEquals(VList.of(1, 2, 3, 4), folded.toList());
    }

    @Test void testEq01() {
        Assertions.assertEquals(VChain.empty(), VChain.empty());
    }

    @Test void testEq02() {
        var c1 = VChain.of(1, 2, 3, 4, 5);
        Assertions.assertEquals(c1, c1);
    }

    @Test void testEq03() {
        var c1 = VChain.of(1, 2, 3, 4, 5);
        var c2 = VChain.of(1, 2, 3, 4, 5);
        Assertions.assertEquals(c1, c2);
    }

    @Test void testEq04() {
        var c1 = VChain.of(1).concat(VChain.of(2));
        var c2 = VChain.of(1, 2);
        Assertions.assertEquals(c1, c2);
    }

    @Test void testEq05() {
        var list = VList.of(       //
                VChain.of(1), //
                VChain.of(2), //
                VChain.of(3), //
                VChain.of(4), //
                VChain.of(5)  //
        );
        var c1 = list.foldLeft(VChain.<Integer>empty(), VChain::concat);
        var c2 = VChain.of(1, 2, 3, 4, 5);
        Assertions.assertEquals(c1, c2);
    }

    @Test void testEq06() {
        var c1 = VChain.of(1, 2, 3, 4, 5);
        var c2 = VChain.of(1, 1, 3, 4, 5);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test void testEq07() {
        var c1 = VChain.of(1).concat(VChain.of(3));
        var c2 = VChain.of(1, 2);
        Assertions.assertNotEquals(c1, c2);
    }

/*
    @Test void testEq08() {
        var list = VList.of(       //
                VChain.of(1), //
                VChain.of(2), //
                VChain.of(3), //
                VChain.of(4), //
                VChain.of(5) //
        );
        var c1 = list.foldLeft(VChain.<Integer>empty(), VChain::concat);
        var c2 = VChain.of(1, 2, 3, 4, 6);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test void testEq09() {
        var c1 = VChain.of(1, 2, 3, 4, 5);
        var c2 = VChain.of(1, 2, 3, 4, 5);
        Assertions.assertEquals(c1, c2);
    }

    @Test void testEq10() {
        var c1 = VChain.of(1, 2, 3, 4, 5);
        var c2 = VChain.of(2, 2, 3, 4, 6);
        Assertions.assertNotEquals(c1, c2);
    }

    @Test void testIsEmpty01() {
        Assertions.assertTrue(VChain.<Integer>empty().isEmpty());
    }

    @Test void testIsEmpty02() {
        Assertions.assertFalse(VChain.of(1).isEmpty());
    }

    @Test void testIsEmpty03() {
        Assertions.assertFalse(VChain.of(1, 2).isEmpty());
    }

    @Test void testHead01() {
        Assertions.assertEquals(Maybe.none(), VChain.<Integer>empty().head());
    }

    @Test void testHead02() {
        Assertions.assertEquals(Maybe.of(1), VChain.of(1).head());
    }

    @Test void testHead03() {
        Assertions.assertEquals(Maybe.of(2), VChain.of(2, 1).head());
    }

    @Test void testHead04() {
        Assertions.assertEquals(Maybe.of(3), VChain.of(3, 2, 1).head());
    }

    @Test void testLength01() {
        Assertions.assertEquals(0, VChain.<Integer>empty().length());
    }

    @Test void testLength02() {
        Assertions.assertEquals(1, VChain.of(1).length());
    }

    @Test void testLength03() {
        Assertions.assertEquals(2, VChain.of(1, 2).length());
    }

    @Test void testLength04() {
        Assertions.assertEquals(3, VChain.of(1, 2, 3).length());
    }
/*
    @Test void testMap01() {
        Assertions.assertEquals(
                VChain.<Boolean>empty(),
                VChain.<Integer>empty().map(i -> i > 2)
        );
    }

    @Test void testMap02() {
        Assertions.assertEquals(
                of(false),
                of(1).map(i -> i > 2)
        );
    }

    @Test void testMap03() {
        Assertions.assertEquals(
                of(true),
                of(3).map(i -> i > 2)
        );
    }

    @Test void testMap04() {
        Assertions.assertEquals(
                of(false, false),
                of(1, 2).map(i -> i > 2)
        );
    }

    @Test void testMap05() {
        Assertions.assertEquals(
                of(false, true),
                of(1, 8).map(i -> i > 2)
        );
    }

    @Test void testMap06() {
        Assertions.assertEquals(
                of(true, false),
                of(8, 1).map(i -> i > 2)
        );
    }

    @Test void testMap07() {
        Assertions.assertEquals(
                of(true, true),
                of(7, 8).map(i -> i > 2)
        );
    }

    @Test void testForeach01() {
        final int[] r = { 21 };
        VChain.<Integer>empty().forEach(x -> r[0] = x);
        Assertions.assertEquals(21, r[0]);
    }

    @Test void testForeach02() {
        final int[] r = { 21 };
        of(1, 2, 3).forEach(x -> r[0] = x);
        Assertions.assertEquals(3, r[0]);
    }

    @Test void testExists01() {
        Assertions.assertFalse(VChain.<Integer>empty().exists(i -> i > 3));
    }

    @Test void testExists02() {
        Assertions.assertFalse(of(1).exists(i -> i > 3));
    }

    @Test void testExists03() {
        Assertions.assertTrue(of(5).exists(i -> i > 3));
    }

    @Test void testExists04() {
        Assertions.assertFalse(of(1, 2).exists(i -> i > 3));
    }

    @Test void testExists05() {
        Assertions.assertTrue(of(1, 6).exists(i -> i > 3));
    }

    @Test void testExists06() {
        Assertions.assertTrue(of(6, 1).exists(i -> i > 3));
    }

    @Test void testExists07() {
        Assertions.assertTrue(of(16, 6).exists(i -> i > 3));
    }

    @Test void testExists08() {
        Assertions.assertFalse(of(1, -9, 3).exists(i -> i > 3));
    }

    @Test void testExists09() {
        Assertions.assertTrue(of(1, 9, 3).exists(i -> i > 3));
    }

    @Test void testMkString01() {
        Assertions.assertEquals("", VChain.<Integer>empty().mkString("+"));
    }

    @Test void testMkString02() {
        Assertions.assertEquals("1+2+3", of(1, 2, 3).mkString("+"));
    }

    @Test void testMkString03() {
        VChain<Integer> chain = of(1, 2, 3, 4, 5);
        Assertions.assertEquals("1-2-3-4-5", chain.mkString("-"));
    }

    @Test void testToString01() {
        Assertions.assertEquals("Chain[]", VChain.<Integer>empty().toString());
    }

    @Test void testToString02() {
        Assertions.assertEquals(
                "Chain[]",
                VChain.from(List.<Integer>of()).toString()
        );
    }

    @Test void testToString03() {
        Assertions.assertEquals(
                "Chain[1, 2, 3]",
                of(1, 2, 3).toString()
        );
    }

    @Test void testToString04() {
        VChain<Integer> chain = List.of(of(1), of(2), of(3), of(4), of(5))
                .stream().reduce(VChain.empty(), VChain::concat);
        Assertions.assertEquals("Chain[1, 2, 3, 4, 5]", chain.toString());
    }

    @Test void testToString05() {
        VChain<Integer> chain = of(1)
                .concat(of(2))
                .concat(of(3))
                .concat(of(4))
                .concat(of(5));
        Assertions.assertEquals("Chain[1, 2, 3, 4, 5]", chain.toString());
    }

    @Test void testToString06() {
        VChain<Integer> chain = VChain.<Integer>empty()
                .concat(of(1))
                .concat(VChain.from(List.<Integer>of()))
                .concat(VChain.from(List.of(2, 3)))
                .concat(VChain.from(List.of(4, 5)));
        Assertions.assertEquals("Chain[1, 2, 3, 4, 5]", chain.toString());
    }

 */
}
