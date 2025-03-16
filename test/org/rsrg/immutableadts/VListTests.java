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
}