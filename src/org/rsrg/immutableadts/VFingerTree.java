package org.rsrg.immutableadts;

public interface VFingerTree {

    // todo (maybe)
    //      https://andrew.gibiansky.com/blog/haskell/finger-trees/

    sealed interface Node<E, A> {
        record Tip<E, A>(E entry, A ann) implements Node<E, A> {}
        record Node2<E, A>(A ann, Node<E, A> s1, Node<E, A> s2) implements Node<E, A> {}
        record Node3<E, A>(A ann, Node<E, A> s1, Node<E, A> s2, Node<E, A> s3) implements Node<E, A> {}
    }

    // one to four ordered nodes
    sealed interface Digit<E, A> {
        record One<E, A>(Node<E, A> s1)
                implements Digit<E, A> {}
        record Two<E, A>(Node<E, A> s1, Node<E, A> s2)
                implements Digit<E, A> {}
        record Three<E, A>(Node<E, A> s1, Node<E, A> s2, Node<E, A> s3)
                implements Digit<E, A> {}
        record Four<E, A>(Node<E, A> s1, Node<E, A> s2, Node<E, A> s3, Node<E, A> s4)
                implements Digit<E, A> {}
    }

    sealed interface FingerTreeStruc<E, A> {
        final class Empty<E, A>     implements FingerTreeStruc<E, A> {
            public static final Empty<?, ?> Instance = new Empty<>();
            private Empty() {}
        }
        @SuppressWarnings("unchecked") static <E1, A1> FingerTreeStruc<E1, A1> empty() {
            return (FingerTreeStruc<E1, A1>) Empty.Instance;
        }
        record Single<E, A>(Node<E, A> ann)  implements FingerTreeStruc<E, A> {}
        record Deep<E, A>(A ann, // measure for whole subtree
                Digit<E, A> leftNodeGroup,
                FingerTreeStruc<E, A> finger,
                Digit<E, A> rightNodeGroup)  implements FingerTreeStruc<E, A> {}
    }

    interface Measured<E1, A1> {
        A1 identity();
        A1 combine(A1 left, A1 right);
        A1 measureOf(E1 elem);
    }
    // gmd = get measure (of) node

}
