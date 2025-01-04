package org.rsrg.mixfix.sorbet;

import org.example.ast.Node;
import org.rsrg.mixfix.sorbet.ast.AstNode;

public class QueryRunner {

    public static <R> R runQuery(AstNode node, Query<R> q) {
        var here = q.map(node);
        return switch (node) {
            case AstNode.Literal l -> here;
            case AstNode.Var v -> here;
            case AstNode.BinOp opNode -> {
                var leftResult = runQuery(opNode.left(), q);
                var rightResult = runQuery(opNode.right(), q);
                yield q.reduce(q.reduce(leftResult, rightResult), here);
            }
            case AstNode.fn -> {
                // We'll fold over the entire body
                var acc = here;
                for (var stmt : fn.body()) {
                    var partial = runQuery(stmt, q);
                    acc = q.reduce(acc, partial);
                }
                yield acc;
            }
        };
    }
}