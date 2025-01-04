package org.rsrg.mixfix.sorbet;

import org.rsrg.mixfix.sorbet.ast.AstNode;

import java.util.HashMap;
import java.util.Map;

public class AstRegistry {

    // Simple global cache: fqn -> AstNode
    private final Map<String, AstNode> cache = new HashMap<>();

    // EXTEND HERE: you might want concurrency (ConcurrentHashMap),
    // or more advanced invalidation strategies, versioning, etc.

    public AstNode getOrNull(String fqn) {
        return cache.get(fqn);
    }

    public void put(String fqn, AstNode node) {
        cache.put(fqn, node);
    }
}