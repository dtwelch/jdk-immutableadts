package org.rsrg.immutableadts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class BalancedBstTests {

    private <A> ArrayList<A> inOrderTraversal(BalancedBst<A> tree) {
        var elements = new ArrayList<A>();
        tree.fold(tree, elements, (acc, a) -> {
            acc.add(a);
            return acc;
        });
        return elements;
    }

    @Test public void testEmptyTree() {
        var tree = BalancedBst.<Integer>empty();
        Assertions.assertTrue(tree.null_(), "Newly created tree should be empty.");
        Assertions.assertEquals(0, tree.size(), "Size of empty tree should be 0.");
    }

    @Test public void testInsertElements() {
        var tree = BalancedBst.<Integer>empty();

        Assertions.assertFalse(tree.contains(10), "Tree should not contain 10 before insertion.");
        tree = tree.insert(10);
        Assertions.assertTrue(tree.contains(10), "Tree should contain 10 after insertion.");
        Assertions.assertFalse(tree.null_(), "Tree should not be empty after insertion.");
        Assertions.assertEquals(1, tree.size(), "Size should be 1 after one insertion.");

        Assertions.assertFalse(tree.contains(5), "Tree should not contain 5 before insertion.");
        tree = tree.insert(5);
        Assertions.assertTrue(tree.contains(5), "Tree should contain 5 after insertion.");

        Assertions.assertFalse(tree.contains(15), "Tree should not contain 15 before insertion.");
        tree = tree.insert(15);
        Assertions.assertTrue(tree.contains(15), "Tree should contain 15 after insertion.");

        Assertions.assertEquals(3, tree.size(), "Size should be 3 after three insertions.");

        var elements = inOrderTraversal(tree);
        Assertions.assertTrue(elements.contains(10), "Tree should contain 10.");
        Assertions.assertTrue(elements.contains(5), "Tree should contain 5.");
        Assertions.assertTrue(elements.contains(15), "Tree should contain 15.");
    }

    @Test public void testInsertDuplicates() {
        var tree = BalancedBst.<Integer>empty();

        Assertions.assertFalse(tree.contains(10), "tree should not contain 10 before insertion.");
        tree = tree.insert(10);
        Assertions.assertTrue(tree.contains(10), "tree should contain 10 after insertion.");
        Assertions.assertEquals(1, tree.size(), "size should be 1 after first insertion.");

        Assertions.assertTrue(tree.contains(10), "tree should contain 10 before duplicate insertion.");
        tree = tree.insert(10);
        Assertions.assertTrue(tree.contains(10), "tree should still contain 10 after duplicate insertion.");
        Assertions.assertEquals(1, tree.size(), "size should remain 1 after inserting a duplicate.");

        tree = tree.insert(10);
        Assertions.assertTrue(tree.contains(10), "tree should still contain 10 after duplicate insertion.");
        Assertions.assertEquals(1, tree.size(), "size should remain 1 after inserting a duplicate.");
    }

    @Test public void testInOrderTraversalSorted() {
        var tree = BalancedBst.<Integer>empty();
        int[] keys = {20, 10, 30, 5, 15, 25, 35};
        for (int key : keys) {
            Assertions.assertFalse(tree.contains(key), "tree should not contain " + key + " before insertion.");
            tree = tree.insert(key);
            Assertions.assertTrue(tree.contains(key), "tree should contain " + key + " after insertion.");
        }

        var elements = inOrderTraversal(tree);
        var sorted = List.of(5, 10, 15, 20, 25, 30, 35);
        Assertions.assertEquals(sorted, elements, "in-order traversal should yield sorted elements.");
    }

    @Test public void testSize() {
        var tree = BalancedBst.<String>empty();
        Assertions.assertEquals(0, tree.size(), "Initial size should be 0.");

        Assertions.assertFalse(tree.contains("apple"), "Tree should not contain 'apple' before insertion.");
        tree = tree.insert("apple");
        Assertions.assertTrue(tree.contains("apple"), "Tree should contain 'apple' after insertion.");
        Assertions.assertEquals(1, tree.size(), "Size should be 1 after one insertion.");

        Assertions.assertFalse(tree.contains("banana"), "Tree should not contain 'banana' before insertion.");
        tree = tree.insert("banana");
        Assertions.assertTrue(tree.contains("banana"), "Tree should contain 'banana' after insertion.");

        Assertions.assertFalse(tree.contains("cherry"), "Tree should not contain 'cherry' before insertion.");
        tree = tree.insert("cherry");
        Assertions.assertTrue(tree.contains("cherry"), "Tree should contain 'cherry' after insertion.");

        Assertions.assertEquals(3, tree.size(), "Size should be 3 after three insertions.");

        Assertions.assertTrue(tree.contains("banana"), "Tree should contain 'banana' before duplicate insertion.");
        tree = tree.insert("banana");
        Assertions.assertTrue(tree.contains("banana"), "Tree should still contain 'banana' after duplicate insertion.");
        Assertions.assertEquals(3, tree.size(), "Size should remain 3 after inserting a duplicate.");
    }

    @Test public void testLargeNumberOfInsertions() {
        var tree = BalancedBst.<Integer>empty();
        var n = 1000;
        for (int i = 0; i < n; i++) {
            Assertions.assertFalse(tree.contains(i), "Tree should not contain " + i + " before insertion.");
            tree = tree.insert(i);
            Assertions.assertTrue(tree.contains(i), "Tree should contain " + i + " after insertion.");
        }

        Assertions.assertEquals(n, tree.size(), "Size should be " + n + " after insertions.");

        var elements = inOrderTraversal(tree);
        for (int i = 0; i < n; i++) {
            Assertions.assertEquals(i, elements.get(i), "Element at index " + i + " should be " + i + ".");
        }

        for (int i = 0; i < n; i++) {
            Assertions.assertTrue(tree.contains(i), "Tree should contain " + i + " before duplicate insertion.");
            tree = tree.insert(i);
            Assertions.assertTrue(tree.contains(i), "Tree should still contain " + i + " after duplicate insertion.");
            Assertions.assertEquals(n, tree.size(), "Size should remain " + n + " after inserting a duplicate.");
        }
    }

    @Test public void testCustomComparator() {
        var reverseOrder = Comparator.<String>reverseOrder();
        var tree = BalancedBst.<String>empty(reverseOrder);

        Assertions.assertFalse(tree.contains("apple"), "Tree should not contain 'apple' before insertion.");
        tree = tree.insert("apple");
        Assertions.assertTrue(tree.contains("apple"), "Tree should contain 'apple' after insertion.");

        Assertions.assertFalse(tree.contains("banana"), "Tree should not contain 'banana' before insertion.");
        tree = tree.insert("banana");
        Assertions.assertTrue(tree.contains("banana"), "Tree should contain 'banana' after insertion.");

        Assertions.assertFalse(tree.contains("cherry"), "Tree should not contain 'cherry' before insertion.");
        tree = tree.insert("cherry");
        Assertions.assertTrue(tree.contains("cherry"), "Tree should contain 'cherry' after insertion.");

        var elements = inOrderTraversal(tree);

        var expected = List.of("cherry", "banana", "apple");
        Assertions.assertEquals(expected, elements, "Elements should be in reverse sorted order.");
    }

    @Test public void testFactoryOfMethod() {
        Integer[] keys = {10, 5, 15, 3, 7, 12, 18};
        var tree = BalancedBst.of(Comparator.naturalOrder(), keys);

        Assertions.assertEquals(7, tree.size(), "Size should match the number of unique elements.");

        var elements = inOrderTraversal(tree);
        var sorted = List.of(3, 5, 7, 10, 12, 15, 18);
        Assertions.assertEquals(sorted, elements, "In-order traversal should yield sorted elements.");
    }

    @Test public void testFindMethod() {
        var tree = BalancedBst.<Integer>empty();

        tree = tree.insert(10).insert(5).insert(15);

        var found10 = tree.find(10);
        var found5 = tree.find(5);
        var found15 = tree.find(15);

        Assertions.assertTrue(found10.isDefined(), "find should return a value for key 10.");
        Assertions.assertEquals(10, found10.get(), "found value for key 10 should be 10.");

        Assertions.assertTrue(found5.isDefined(), "find should return a value for key 5.");
        Assertions.assertEquals(5, found5.get(), "found value for key 5 should be 5.");

        Assertions.assertTrue(found15.isDefined(), "find should return a value for key 15.");
        Assertions.assertEquals(15, found15.get(), "found value for key 15 should be 15.");

        // test finding a non-existing key
        Maybe<Integer> found20 = tree.find(20);
        Assertions.assertFalse(found20.isDefined(), "find should return empty for non-existing key 20.");
    }

    @Test public void testDeleteElements() {
        // build a tree with several keys
        var tree = BalancedBst.<Integer>empty();
        int[] keys = {20, 10, 30, 5, 15, 25, 35};
        for (var key : keys) {
            tree = tree.insert(key);
        }
        Assertions.assertEquals(7, tree.size(), "Size should be 7 after insertions.");

        // delete a leaf node (e.g., 5)
        tree = tree.delete(5);
        Assertions.assertFalse(tree.contains(5), "Tree should not contain 5 after deletion.");
        Assertions.assertEquals(6, tree.size(), "Size should be 6 after deleting 5.");

        // delete a node with one child (try deleting 30, assuming its right child is 35)
        tree = tree.delete(30);
        Assertions.assertFalse(tree.contains(30), "Tree should not contain 30 after deletion.");
        Assertions.assertEquals(5, tree.size(), "Size should be 5 after deleting 30.");

        // delete a node with two children (delete 20)
        tree = tree.delete(20);
        Assertions.assertFalse(tree.contains(20), "Tree should not contain 20 after deletion.");
        Assertions.assertEquals(4, tree.size(), "Size should be 4 after deleting 20.");

        // verify in-order traversal remains sorted and only contains remaining keys
        ArrayList<Integer> elements = inOrderTraversal(tree);
        List<Integer> expected = List.of(10, 15, 25, 35);
        Assertions.assertEquals(expected, elements, "In-order traversal should yield sorted elements after deletions.");

        // deleting a non-existent key should not change the tree
        BalancedBst<Integer> before = tree;
        tree = tree.delete(100);
        Assertions.assertEquals(before.size(), tree.size(), "Deleting a non-existent key should not change the tree.");
    }
}