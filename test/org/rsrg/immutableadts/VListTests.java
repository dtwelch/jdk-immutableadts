package org.rsrg.immutableadts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class VListTests {

    @Test void emptyAppendEmptyShouldReturnEmpty() {
        var list1 = VList.<Integer>empty();
        var list2 = VList.<Integer>empty();
        var appended = list1.append(list2);
        var expected = VList.<Integer>empty();
        Assertions.assertEquals(expected, appended);
    }

    @Test void emptyAppendNonEmptyShouldReturnNonEmpty() {
        var list1 = VList.<Integer>empty();
        var list2 = VList.of(1, 2, 3);
        var appended = list1.append(list2);
        var expected = VList.of(1, 2, 3);
        Assertions.assertEquals(expected, appended);
    }

    @Test void nonEmptyAppendEmptyShouldReturnNonEmpty() {
        var list1 = VList.of(1, 2, 3);
        var list2 = VList.<Integer>empty();
        var appended = list1.append(list2);
        var expected = VList.of(1, 2, 3);
        Assertions.assertEquals(expected, appended);
    }

    @Test void nonEmptyAppendNonEmptyShouldReturnCombinedList() {
        var list1 = VList.of(1, 2, 3);
        var list2 = VList.of(4, 5, 6);
        var appended = list1.append(list2);
        var expected = VList.of(1, 2, 3, 4, 5, 6);
        Assertions.assertEquals(expected, appended);
    }

    @Test void appendShouldUpdateSize() {
        var list1 = VList.of(1, 2, 3);
        var list2 = VList.of(4, 5);
        var appended = list1.append(list2);
        var expectedSize = 5;
        Assertions.assertEquals(expectedSize, appended.size());
    }

    @Test void appendEmptyToEmptyShouldMaintainSize() {
        var list1 = VList.<Integer>empty();
        var list2 = VList.<Integer>empty();
        var appended = list1.append(list2);
        var expectedSize = 0;
        Assertions.assertEquals(expectedSize, appended.size());
    }

    @Test void appendShouldBeImmutable() {
        var list1 = VList.of(1, 2, 3);
        var list2 = VList.of(4, 5, 6);
        var appended = list1.append(list2);
        var expected1 = VList.of(1, 2, 3);
        var expected2 = VList.of(4, 5, 6);
        Assertions.assertEquals(expected1, list1);
        Assertions.assertEquals(expected2, list2);
    }

    @Test void appendDifferentOrderShouldMaintainCombinedOrder() {
        var list1 = VList.of(3, 2, 1);
        var list2 = VList.of(6, 5, 4);
        var appended = list1.append(list2);
        var expected = VList.of(3, 2, 1, 6, 5, 4);
        Assertions.assertEquals(expected, appended);
    }

    @Test void appendLargeListsShouldHaveCorrectSize() {
        var list1 = VList.<Integer>empty();
        for (var i = 0; i < 1000; i++) {
            list1 = list1.append(VList.of(i));
        }
        var list2 = VList.<Integer>empty();
        for (var i = 1000; i < 2000; i++) {
            list2 = list2.append(VList.of(i));
        }
        var appended = list1.append(list2);
        var expectedSize = 2000;
        Assertions.assertEquals(expectedSize, appended.size());
    }

    @Test void foldLeftShouldAccumulateCorrectly() {
        var list = VList.of(1, 2, 3, 4);
        int sum = list.foldLeft(0, Integer::sum);
        Assertions.assertEquals(10, sum);
    }

    @Test void foldRightShouldAccumulateCorrectly() {
        var list = VList.of(1, 2, 3, 4);
        int sum = list.foldRight(0, Integer::sum);
        Assertions.assertEquals(10, sum);
    }

    @Test void foldLeftShouldPreserveOrder() {
        var list = VList.of("a", "b", "c");
        String concat = list.foldLeft("", (acc, x) -> acc + x);
        Assertions.assertEquals("abc", concat);
    }

    @Test void foldRightShouldPreserveOrder() {
        var list = VList.of("a", "b", "c");
        String concat = list.foldRight("", (x, acc) -> x + acc);
        Assertions.assertEquals("abc", concat);
    }

    @Test void mapShouldApplyFunctionCorrectly() {
        var list = VList.of(1, 2, 3);
        var doubled = list.map(x -> x * 2);
        var expected = VList.of(2, 4, 6);
        Assertions.assertEquals(expected, doubled);
    }

    @Test void mkStringSingleElement() {
        var list = VList.of(1);
        var result = list.mkString("[", ", ", "]");
        Assertions.assertEquals("[1]", result);
    }

    @Test void mkStringTwoElements() {
        var list = VList.of(1, 2);
        var result = list.mkString("[", ", ", "]");
        Assertions.assertEquals("[1, 2]", result);
    }

    @Test void mkStringThreeElements() {
        var list = VList.of(1, 2, 3);
        var result = list.mkString("[", ", ", "]");
        Assertions.assertEquals("[1, 2, 3]", result);
    }

    @Test void mkStringEmptyList() {
        var list = VList.empty();
        var result = list.mkString("[", ", ", "]");
        Assertions.assertEquals("[]", result);
    }

    @Test void mkStringCustomDelimiters() {
        var list = VList.of("a", "b", "c");
        var result = list.mkString("<", "|", ">");
        Assertions.assertEquals("<a|b|c>", result);
    }

    @Test void mkStringOneElementCustom() {
        var list = VList.of(42);
        var result = list.mkString("{", " - ", "}");
        Assertions.assertEquals("{42}", result);
    }
}