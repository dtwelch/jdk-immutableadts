package org.rsrg.mixfix.immutableadts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

/** Tests for the (in order) iterator for {@link BalancedBst} provides. */
public class BstIteratorTests {

    @Test public void testEmptyTreeIterator() {
        BalancedBst<Integer> emptyTree = BalancedBst.empty(Integer::compareTo);
        var iterator = emptyTree.iterator();
        Assertions.assertFalse(iterator.hasNext(),
                "Iterator should have no " + "elements for an empty tree");
    }

    @Test public void testSingleElementIterator() {
        var singleElementTree =
                BalancedBst.<Integer>of(Integer::compareTo, 42);
        var iterator = singleElementTree.iterator();
        Assertions.assertTrue(iterator.hasNext(),
                "Iterator should have one element");
        Assertions.assertEquals(42, iterator.next(), "Iterator should return "
                + "the single inserted element");
        Assertions.assertFalse(iterator.hasNext(),
                "Iterator should have no more elements after the single "
                        + "element");
    }

    @Test void testMultipleElementsIterator() {
        Integer[] elements = {5, 3, 7, 2, 4, 6, 8};
        var tree = BalancedBst.of(Integer::compareTo, elements);

        // Expected order after in-order traversal: 2, 3, 4, 5, 6, 7, 8
        int[] expectedOrder = {2, 3, 4, 5, 6, 7, 8};

        var iterator = tree.iterator();
        var index = 0;
        while (iterator.hasNext()) {
            Assertions.assertTrue(index < expectedOrder.length,
                    "iterator " + "has" + " more elements than expected");
            Integer actual = iterator.next();
            Assertions.assertEquals(expectedOrder[index], actual,
                    "iterator " + "returned unexpected element at index " + index);
            index++;
        }
        Assertions.assertEquals(expectedOrder.length, index,
                "iterator did " + "not return the expected number of elements");
    }

    @Test public void testIteratorNoSuchElementException() {
        var tree = BalancedBst.<Integer>of(Integer::compareTo, 1, 2, 3);
        var iterator = tree.iterator();

        // Consume all elements
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(1, iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(2, iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(3, iterator.next());
        Assertions.assertFalse(iterator.hasNext());

        // call next() should throw exception
        Assertions.assertThrows(NoSuchElementException.class, iterator::next,
                "Iterator should throw NoSuchElementException when no " +
                        "elements left");
    }

    @Test public void testMultipleIterators() {
        Integer[] elements = {10, 5, 15, 3, 7, 12, 18};
        var tree = BalancedBst.of(Integer::compareTo, elements);

        Integer[] expectedOrder = {3, 5, 7, 10, 12, 15, 18};

        var iterator1 = tree.iterator();
        var iterator2 = tree.iterator();

        for (int i = 0; i < expectedOrder.length; i++) {
            Assertions.assertTrue(iterator1.hasNext(), "iterator1 should " +
                    "have next element");
            Assertions.assertEquals(expectedOrder[i], iterator1.next(),
                    "iterator1 returned unexpected element at index " + i);
        }
        Assertions.assertFalse(iterator1.hasNext(), "iterator1 should have " +
                "no more elements");

        for (int i = 0; i < expectedOrder.length; i++) {
            Assertions.assertTrue(iterator2.hasNext(), "iterator2 should " +
                    "have next element");
            Assertions.assertEquals(expectedOrder[i], iterator2.next(),
                    "iterator2 returned unexpected element at index " + i);
        }
        Assertions.assertFalse(iterator2.hasNext(), "iterator2 should have " +
                "no more elements");
    }

    @Test public void testIteratorOrderWithRandomInsertion() {
        Integer[] insertionOrder = {50, 30, 70, 20, 40, 60, 80};
        var tree = BalancedBst.of(Integer::compareTo, insertionOrder);

        int[] expectedOrder = {20, 30, 40, 50, 60, 70, 80};

        var iterator = tree.iterator();
        var index = 0;
        while (iterator.hasNext()) {
            Assertions.assertTrue(index < expectedOrder.length, "iterator has" +
                    " more elements than expected");
            var actual = iterator.next();
            Assertions.assertEquals(expectedOrder[index], actual, "iterator " +
                    "returned unexpected element at index " + index);
            index++;
        }
        Assertions.assertEquals(expectedOrder.length, index, "iterator did " +
                "not return the expected number of elements");
    }
}
