package org.rsrg.mixfix.parsing;

public final class Mixfix {
    sealed interface Associativity {
        enum LeftAssociative    implements Associativity {LeftAssocInst}
        enum NonAssociative     implements Associativity {NonAssocInst}
        enum RightAssociative   implements Associativity {RightAssocInst}
    }

    sealed interface Fixity {
        enum Prefix                             implements Fixity {}
        record Infix(Associativity assocLvl)    implements Fixity {}
        enum Postfix                            implements Fixity {}
        enum Closed                             implements Fixity {}
    }

    public Operator x() {
        return null;
    }

    record Operator(int arity) {

    }
}
