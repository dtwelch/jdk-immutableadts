package org.rsrg.mixfix.sorbet;

import org.rsrg.mixfix.sorbet.ast.AstNode;

/**
 * A generic map reduce style interface for AST queries.
 * R is the result type of the query.
 */
public interface Query<R> {
    R map(AstNode node);
    R reduce(R leftResult, R rightResult);
}
