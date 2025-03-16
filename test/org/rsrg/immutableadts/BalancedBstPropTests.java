package org.rsrg.immutableadts;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.*;

public class BalancedBstPropTests {

    // property: construction yields a balanced tree
    //              (satisfying invariants AA1-AA4)
    // ∀ (list of integers E) where T = BalancedBst.of(E).
    //  then for each node N in T at level l
    //  1) AA1: level(left(N)) = l - 1
    //  2) AA2: level(right(N)) ∈ {l, l - 1}
    //  3) AA3: level(right(right(N))) < l
    //  4) AA4: if l > 1 then left(N) /= Empty and right(N) /= Empty
    @Property void aaTreeInvariants(@ForAll List<Integer> elements) {
        BalancedBst<Integer> tree = BalancedBst.of(elements.toArray(new Integer[0]));

        // check invariants on the underlying representation.
        // note: assumed that the 'rep' field is accessible (e.g., package‑private)
        assertTrue(checkAA1AA2(tree.rep), "AA1, AA2 invariants broken");
        assertTrue(checkAA3AA4(tree.rep), "AA3, AA4 invariants broken");
    }

    @Property void duplicateInsertionsDontIncreaseSize(@ForAll List<Integer> elements) {
        BalancedBst<Integer> tree = BalancedBst.empty();
        for (Integer e : elements) {
            tree = tree.insert(e);
            tree = tree.insert(e); // duplicate insertion
        }
        // the size should equal the number of unique elements ...
        HashSet<Integer> distinct = new HashSet<>(elements);
        Assertions.assertEquals(distinct.size(), tree.size(), "Duplicate insertions should not increase size");

        // ... and the in-order traversal should match the sorted unique set
        List<Integer> inOrder = inOrderTraversal(tree);
        List<Integer> sortedDistinct = new ArrayList<>(distinct);
        Collections.sort(sortedDistinct);
        Assertions.assertEquals(sortedDistinct, inOrder, "In-order traversal should yield sorted unique elements");
    }

    @Property void deletionOfNonExistentKeysIsNoOp(@ForAll List<Integer> insertElements, @ForAll List<Integer> deleteElements) {
        // insert keys into the tree
        var tree = BalancedBst.<Integer>empty();
        for (var e : insertElements) {
            tree = tree.insert(e);
        }
        var sizeBefore = tree.size();
        var inOrderBefore = inOrderTraversal(tree);

        // delete keys that are NOT in the tree
        var insertedSet = new HashSet<Integer>(insertElements);
        for (var e : deleteElements) {
            if (!insertedSet.contains(e)) {
                tree = tree.delete(e);
            }
        }
        Assertions.assertEquals(sizeBefore, tree.size(), "Deleting non-existent keys should not change the size");
        Assertions.assertEquals(inOrderBefore, inOrderTraversal(tree), "In-order traversal should remain unchanged");
    }

    @Property void interleavedInsertionsAndDeletionsPreserveInvariants(@ForAll List<Integer> insertElements, @ForAll List<Integer> deleteElements) {
        var tree = BalancedBst.<Integer>empty();
        for (var e : insertElements) {
            tree = tree.insert(e);
        }
        for (var e : deleteElements) {
            tree = tree.delete(e);
        }

        // check invariants
        assertTrue(checkAA1AA2(tree.rep), "AA1, AA2 invariants broken after deletions");
        assertTrue(checkAA3AA4(tree.rep), "AA3, AA4 invariants broken after deletions");

        // verify that the in-order traversal is sorted and equals (insertElements - deleteElements)
        var expected = new HashSet<Integer>(insertElements);
        expected.removeAll(deleteElements);
        var sortedExpected = new ArrayList<Integer>(expected);
        Collections.sort(sortedExpected);
        var inOrder = inOrderTraversal(tree);
        Assertions.assertEquals(sortedExpected, inOrder,
                "In-order traversal should match expected sorted elements");
    }

    // --- helpers: inOrderTraversal, checkAA1AA2, checkAA3AA4 ---

    private ArrayList<Integer> inOrderTraversal(BalancedBst<Integer> tree) {
        ArrayList<Integer> elements = new ArrayList<>();
        tree.fold(tree, elements, (acc, a) -> {
            acc.add(a);
            return acc;
        });
        return elements;
    }

    // helper: get the level of a node (empty nodes have level 0)
    private int levelOf(AlgebraicTr<Integer> t) {
        if (t instanceof AlgebraicTr.Empty) {
            return 0;
        } else if (t instanceof AlgebraicTr.Node<Integer> node) {
            return node.lvl();
        }
        return 0;
    }

    // check AA1: left child's level is exactly one less than parent's level
    // check AA2: right child's level is either the same as parent's (double node)
    //          or one less (single node)
    private boolean checkAA1AA2(AlgebraicTr<Integer> t) {
        if (t instanceof AlgebraicTr.Empty) {
            return true;
        } else if (t instanceof AlgebraicTr.Node<Integer> node) {
            int level = node.lvl();
            int leftLevel = levelOf(node.left());
            int rightLevel = levelOf(node.right());
            boolean aa1 = (leftLevel == level - 1);
            boolean aa2 = (rightLevel == level || rightLevel == level - 1);
            return aa1 && aa2 && checkAA1AA2(node.left()) && checkAA1AA2(node.right());
        }
        return true;
    }

    // check AA3: right child of the right child has a level less than parent's level
    // check AA4: if parent's level > 1 then both children should be non-empty
    private boolean checkAA3AA4(AlgebraicTr<Integer> t) {
        if (t instanceof AlgebraicTr.Empty) {
            return true;
        } else if (t instanceof AlgebraicTr.Node<Integer> node) {
            int lvl = node.lvl();
            int rightRightLevel = 0;
            if (node.right() instanceof AlgebraicTr.Empty) {
                rightRightLevel = 0;
            } else if (node.right() instanceof AlgebraicTr.Node<Integer> rightNode) {
                if (rightNode.right() instanceof AlgebraicTr.Empty) {
                    rightRightLevel = 0;
                } else if (rightNode.right() instanceof AlgebraicTr.Node<Integer> rightRightNode) {
                    rightRightLevel = rightRightNode.lvl();
                }
            }
            boolean aa3 = rightRightLevel < lvl;
            boolean aa4 = lvl <= 1 || (!(node.left() instanceof AlgebraicTr.Empty) && !(node.right() instanceof AlgebraicTr.Empty));
            return aa3 && aa4 && checkAA3AA4(node.left()) && checkAA3AA4(node.right());
        }
        return true;
    }
}