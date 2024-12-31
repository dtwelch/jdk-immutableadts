package org.rsrg.mixfix.immutableadts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BalancedBstTests {

    private <A> List<A> inOrderTraversal(BalancedBst<A> tree) {
        var elements = new ArrayList<A>();
        tree.fold(tree, elements, (acc, a) -> {
            acc.add(a);
            return acc;
        });
        return elements;
    }

    private <A> BalancedBst<A> insertAndAssert(BalancedBst<A> tree, A key, boolean expectInserted) {
        var result = tree.insert(key);
        Assertions.assertEquals(expectInserted, result.second(),
                "insertion of key " + key + (expectInserted ? " should succeed." : " should fail."));
        return result.first();
    }

    @Test public void testEmptyTree() {
        var tree = BalancedBst.<Integer>empty();
        Assertions.assertTrue(tree.null_(), "newly created tree should be empty.");
        Assertions.assertEquals(0, tree.size(), "size of empty tree should be 0.");
    }

    @Test public void testInsertElements() {
        var tree = BalancedBst.<Integer>empty();
        tree = insertAndAssert(tree, 10, true);
        Assertions.assertFalse(tree.null_(), "tree should not be empty after insertion.");
        Assertions.assertEquals(1, tree.size(), "size should be 1 after one insertion.");

        tree = insertAndAssert(tree, 5, true);
        tree = insertAndAssert(tree, 15, true);
        Assertions.assertEquals(3, tree.size(), "size should be 3 after three insertions.");

        var elements = inOrderTraversal(tree);
        Assertions.assertTrue(elements.contains(10), "tree should contain 10.");
        Assertions.assertTrue(elements.contains(5), "tree should contain 5.");
        Assertions.assertTrue(elements.contains(15), "tree should contain 15.");
    }

    @Test public void testInsertDuplicates() {
        var tree = BalancedBst.<Integer>empty();
        tree = insertAndAssert(tree, 10, true);
        Assertions.assertEquals(1, tree.size(), "size should be 1 after first insertion.");

        tree = insertAndAssert(tree, 10, false);
        Assertions.assertEquals(1, tree.size(), "size should remain 1 after inserting a duplicate.");

        tree = insertAndAssert(tree, 10, false);
        Assertions.assertEquals(1, tree.size(), "size should remain 1 after inserting a duplicate.");
    }

    @Test public void testInOrderTraversalSorted() {
        var tree = BalancedBst.<Integer>empty();
        int[] keys = {20, 10, 30, 5, 15, 25, 35};
        for (var key : keys) {
            tree = insertAndAssert(tree, key, true);
        }

        var elements = inOrderTraversal(tree);
        var sorted = List.of(5, 10, 15, 20, 25, 30, 35);
        Assertions.assertEquals(sorted, elements, "in-order traversal should yield sorted elements.");
    }

    @Test public void testSize() {
        var tree = BalancedBst.<String>empty();
        Assertions.assertEquals(0, tree.size(), "initial size should be 0.");

        tree = insertAndAssert(tree, "apple", true);
        Assertions.assertEquals(1, tree.size(), "size should be 1 after one insertion.");

        tree = insertAndAssert(tree, "banana", true);
        tree = insertAndAssert(tree, "cherry", true);
        Assertions.assertEquals(3, tree.size(), "size should be 3 after three insertions.");

        tree = insertAndAssert(tree, "banana", false);
        Assertions.assertEquals(3, tree.size(), "size should remain 3 after inserting a duplicate.");
    }

    @Test public void testLargeNumberOfInsertions() {
        var tree = BalancedBst.<Integer>empty();
        int n = 1000;
        for (var i = 0; i < n; i++) {
            tree = insertAndAssert(tree, i, true);
        }

        Assertions.assertEquals(n, tree.size(), "size should be 1000 after insertions.");

        var elements = inOrderTraversal(tree);
        for (var i = 0; i < n; i++) {
            Assertions.assertEquals(i, elements.get(i), "element at index " + i + " should be " + i + ".");
        }

        for (var i = 0; i < n; i++) {
            tree = insertAndAssert(tree, i, false);
        }

        Assertions.assertEquals(n, tree.size(), "size should remain 1000 after inserting duplicates.");
    }

    @Test public void testCustomComparator() {
        var reverseOrder = Comparator.<String>reverseOrder();
        var tree = BalancedBst.empty(reverseOrder);
        tree = insertAndAssert(tree, "apple", true);
        tree = insertAndAssert(tree, "banana", true);
        tree = insertAndAssert(tree, "cherry", true);

        var elements = inOrderTraversal(tree);
        var expected = List.of("cherry", "banana", "apple");
        Assertions.assertEquals(expected, elements, "elements should be in reverse sorted order.");
    }

    @Test public void testFactoryOfMethod() {
        Integer[] keys = {10, 5, 15, 3, 7, 12, 18};
        var tree = BalancedBst.of(Comparator.<Integer>naturalOrder(), keys);
        Assertions.assertEquals(7, tree.size(), "size should match the number of unique elements.");

        var elements = inOrderTraversal(tree);
        var sorted = List.of(3, 5, 7, 10, 12, 15, 18);
        Assertions.assertEquals(sorted, elements, "in-order traversal should yield sorted elements.");
    }

    @Test public void testFindInEmptyTree() {
        var tree = BalancedBst.<Integer>empty();
        var result = tree.find(10);
        Assertions.assertTrue(result.isEmpty(), "find should return Maybe.none() for any key in an empty tree.");
    }

    @Test public void testFindExistingElements() {
        var tree = BalancedBst.<Integer>empty();
        tree = insertAndAssert(tree, 10, true);
        tree = insertAndAssert(tree, 5, true);
        tree = insertAndAssert(tree, 15, true);

        var find10 = tree.find(10);
        Assertions.assertTrue(find10.isDefined(), "find should return Maybe.of(10) for existing key 10.");
        Assertions.assertEquals(10, find10.get(), "find should return the correct value for key 10.");

        var find5 = tree.find(5);
        Assertions.assertTrue(find5.isDefined(), "find should return Maybe.of(5) for existing key 5.");
        Assertions.assertEquals(5, find5.get(), "find should return the correct value for key 5.");

        var find15 = tree.find(15);
        Assertions.assertTrue(find15.isDefined(), "find should return Maybe.of(15) for existing key 15.");
        Assertions.assertEquals(15, find15.get(), "find should return the correct value for key 15.");
    }

    @Test public void testFindNonExistingElements() {
        var tree = BalancedBst.<Integer>empty();
        tree = insertAndAssert(tree, 10, true);
        tree = insertAndAssert(tree, 5, true);
        tree = insertAndAssert(tree, 15, true);

        var find20 = tree.find(20);
        Assertions.assertTrue(find20.isEmpty(), "find should return Maybe.none() for non-existing key 20.");

        var find0 = tree.find(0);
        Assertions.assertTrue(find0.isEmpty(), "find should return Maybe.none() for non-existing key 0.");

        var findMinus5 = tree.find(-5);
        Assertions.assertTrue(findMinus5.isEmpty(), "find should return Maybe.none() for non-existing key -5.");
    }

    @Test public void testFindAfterMultipleInsertions() {
        var tree = BalancedBst.<String>empty();
        tree = insertAndAssert(tree, "apple", true);
        tree = insertAndAssert(tree, "banana", true);
        tree = insertAndAssert(tree, "cherry", true);
        tree = insertAndAssert(tree, "date", true);
        tree = insertAndAssert(tree, "fig", true);

        var findApple = tree.find("apple");
        Assertions.assertTrue(findApple.isDefined(), "Find should return Maybe.of('apple') for existing key 'apple'.");
        Assertions.assertEquals("apple", findApple.get(), "find should return the correct value for key 'apple'.");

        var findBanana = tree.find("banana");
        Assertions.assertTrue(findBanana.isDefined(), "Find should return Maybe.of('banana') for existing key 'banana'.");
        Assertions.assertEquals("banana", findBanana.get(), "find should return the correct value for key 'banana'.");

        var findCherry = tree.find("cherry");
        Assertions.assertTrue(findCherry.isDefined(), "Find should return Maybe.of('cherry') for existing key 'cherry'.");
        Assertions.assertEquals("cherry", findCherry.get(), "find should return the correct value for key 'cherry'.");

        var findDate = tree.find("date");
        Assertions.assertTrue(findDate.isDefined(), "find should return Maybe.of('date') for existing key 'date'.");
        Assertions.assertEquals("date", findDate.get(), "find should return the correct value for key 'date'.");

        var findFig = tree.find("fig");
        Assertions.assertTrue(findFig.isDefined(), "find should return Maybe.of('fig') for existing key 'fig'.");
        Assertions.assertEquals("fig", findFig.get(), "find should return the correct value for key 'fig'.");

        var findGrape = tree.find("grape");
        Assertions.assertTrue(findGrape.isEmpty(), "find should return Maybe.none() for non-existing key 'grape'.");

        var findElderberry = tree.find("elderberry");
        Assertions.assertTrue(findElderberry.isEmpty(), "find should return Maybe.none() for non-existing key 'elderberry'.");
    }

    @Test public void testDeleteElements() {
        // Placeholder for delete operation tests
    }
}