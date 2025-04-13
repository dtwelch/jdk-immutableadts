package org.rsrg.immutableadts;

public interface VFingerTree {

    // todo (maybe)
    //      https://andrew.gibiansky.com/blog/haskell/finger-trees/

    sealed interface Node<E, A> {
        record Tip<E, A>(E entry, A ann)                                        implements Node<E, A> {}
        record Node2<E, A>(A ann, Node<E, A> s1, Node<E, A> s2)                 implements Node<E, A> {}
        record Node3<E, A>(A ann, Node<E, A> s1, Node<E, A> s2, Node<E, A> s3)  implements Node<E, A> {}
    }

    sealed interface Digit<E, A> {
        record One<E, A>(E entry, A ann)                implements Digit<E, A> {}
        record Two<E, A>(Node<E, A> s1, Node<E, A> s2)  implements Digit<E, A> {}
        record Three<E, A>
    }
}
