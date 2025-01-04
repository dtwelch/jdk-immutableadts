package org.rsrg.mixfix.sorbet.ast;

import org.rsrg.mixfix.immutableadts.VList;

public sealed interface AstNode {
    record Literal(int value)                            implements AstNode {}
    record Var(String name)                              implements AstNode {}
    record BinOp(String op, AstNode left, AstNode right) implements AstNode {}
    // a "function" node with FQN (fully qualified name) and a body
    record Function(String fqn, VList<AstNode> body)     implements AstNode {}
}
