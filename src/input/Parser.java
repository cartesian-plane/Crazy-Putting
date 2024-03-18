package logic;

import java.util.List;

import interpreter.Lox;

import static logic.TokenType.*;

class Parser {

    private static class ParseError extends RuntimeException {}
    
    private final List<Token> tokens;
    private int current = 0;
    
    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr expression() {
        Expr expr = unary();
        TokenType type = peek().type;
        while (match(CONJUNCTION, INCLUSIVE_DISJUNCTION, EXCLUSIVE_DISJUNCTION, IMPLICATION, BIDIRECTIONAL_IMPLICATION)) {
            if(previous().type != type)
                throw error(previous(), "Ambiguous expression.");
            type = previous().type;
            Token operator = previous();
            Expr right = expression();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() {
        if (match(NEGATION)) {
            return new Expr.Unary(previous(), unary()); //operator, right
        }
        return primary();
    }

    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(ATOMIC_PROPOSITION)) return new Expr.Literal(previous().lexeme);
        if (match(LEFT_PARENTHESIS)) {
            Expr expr = expression();
            consume(RIGHT_PARENTHESIS, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }
    
    private ParseError error(Token token, String message) {
        Logic.error(token, message);
        return new ParseError();
    }

    /*
    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                return;
            }
            advance();
        }
    }
    */

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
            return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
            return previous();
    }
    private boolean isAtEnd() {
        return peek().type == EOF;
    }
    private Token peek() {
        return tokens.get(current);
    }
    private Token previous() {
        return tokens.get(current - 1);
    }
}
