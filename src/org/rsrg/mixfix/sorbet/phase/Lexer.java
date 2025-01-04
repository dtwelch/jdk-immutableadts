package org.rsrg.mixfix.sorbet.phase;

import java.util.ArrayList;
import java.util.List;

public final class Lexer {

    public record Token(TokenKind kind, String text) {}

    public enum TokenKind {
        NUMBER, IDENT, PLUS, MINUS, LPAREN, RPAREN, ERROR, EOF
    }

    private final String input;
    private int pos;

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
    }

    public List<Token> tokenize() {
        var tokens = new ArrayList<Token>();
        while (true) {
            var t = nextToken();
            tokens.add(t);
            if (t.kind() == TokenKind.EOF) {
                break;
            }
        }
        return tokens;
    }

    private Token nextToken() {
        skipWhitespace();
        if (pos >= input.length()) {
            return new Token(TokenKind.EOF, "");
        }
        var ch = input.charAt(pos);
        switch (ch) {
            case '(' -> {
                pos++;
                return new Token(TokenKind.LPAREN, "(");
            }
            case ')' -> {
                pos++;
                return new Token(TokenKind.RPAREN, ")");
            }
            case '+' -> {
                pos++;
                return new Token(TokenKind.PLUS, "+");
            }
            case '-' -> {
                pos++;
                return new Token(TokenKind.MINUS, "-");
            }
            default -> {
                if (Character.isDigit(ch)) {
                    var start = pos;
                    while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
                        pos++;
                    }
                    var text = input.substring(start, pos);
                    return new Token(TokenKind.NUMBER, text);
                } else if (Character.isAlphabetic(ch)) {
                    var start = pos;
                    while (pos < input.length() && Character.isAlphabetic(input.charAt(pos))) {
                        pos++;
                    }
                    var text = input.substring(start, pos);
                    return new Token(TokenKind.IDENT, text);
                } else {
                    pos++;
                    return new Token(TokenKind.ERROR, String.valueOf(ch));
                }
            }
        }
    }

    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }
}
