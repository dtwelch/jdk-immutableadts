package org.rsrg.mixfix.immutableadts;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;

public class BalancedBstPropTests {

    @Property void aaTreeInvariants(@ForAll List<Integer> elements) {
        BalancedBst<Integer> tree = BalancedBst.of(elements.toArray(new Integer[0]));

        // check invariants on the underlying representation.
        // note: assumed that the 'rep' field is accessible (e.g., package‑private).
        assertTrue(checkAA1AA2(tree.rep), "AA1, AA2 invariants broken");
        assertTrue(checkAA3AA4(tree.rep), "AA3, AA4 invariants broken");
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
            boolean aa4 = lvl <= 1 || (!(node.left() instanceof AlgebraicTr.Empty)
                    && !(node.right() instanceof AlgebraicTr.Empty));
            return aa3 && aa4 && checkAA3AA4(node.left()) && checkAA3AA4(node.right());
        }
        return true;
    }
}