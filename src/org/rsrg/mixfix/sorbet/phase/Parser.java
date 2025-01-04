package org.rsrg.mixfix.sorbet.phase;

import org.example.arith.Lexer.Token;
import org.example.arith.Lexer.TokenKind;

import java.util.List;
import java.util.ArrayList;

public class Parser {

    private final List<Token> tokens;
    private int pos;
    private final List<String> errors;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.errors = new ArrayList<>();
    }

    public record ParseResult(Expr expr, List<String> errors) {}

    public ParseResult parse() {
        var e = parseExpr();
        if (!isAtEnd()) {
            errors.add("Extra tokens after expression");
        }
        return new ParseResult(e, errors);
    }

    private Expr parseExpr() {
        var left = parsePrimary();
        while (!isAtEnd()) {
            var t = peek();
            if (t.kind() == TokenKind.PLUS || t.kind() == TokenKind.MINUS) {
                advance();
                var right = parsePrimary();
                left = new Expr.Binary(t.kind() == TokenKind.PLUS ? '+' : '-', left, right);
            } else {
                break;
            }
        }
        return left;
    }

    private Expr parsePrimary() {
        if (isAtEnd()) {
            errors.add("Unexpected end of tokens in primary");
            return new Expr.Ident("ERROR");
        }
        var t = peek();
        switch (t.kind()) {
            case NUMBER -> {
                advance();
                try {
                    var value = Integer.parseInt(t.text());
                    return new Expr.NumberLit(value);
                } catch (NumberFormatException ex) {
                    errors.add("Invalid number literal: " + t.text());
                    return new Expr.Ident("ERROR");
                }
            }
            case IDENT -> {
                advance();
                return new Expr.Ident(t.text());
            }
            case LPAREN -> {
                advance();
                var exprInside = parseExpr();
                if (!match(TokenKind.RPAREN)) {
                    errors.add("Missing closing parenthesis");
                }
                return exprInside;
            }
            case ERROR -> {
                advance();
                errors.add("Unrecognized character: " + t.text());
                return new Expr.Ident("ERROR");
            }
            default -> {
                errors.add("Unexpected token: " + t.text());
                advance();
                return new Expr.Ident("ERROR");
            }
        }
    }

    private boolean match(TokenKind kind) {
        if (peek().kind() == kind) {
            advance();
            return true;
        }
        return false;
    }

    private Token peek() {
        if (pos < tokens.size()) {
            return tokens.get(pos);
        }
        return new Token(TokenKind.EOF, "");
    }

    private boolean isAtEnd() {
        return peek().kind() == TokenKind.EOF;
    }

    private void advance() {
        if (!isAtEnd()) pos++;
    }
}