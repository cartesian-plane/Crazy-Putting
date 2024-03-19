package input;

import java.util.List;

import interfaces.IFunc;

import static input.TokenType.*;

class MathParser {

    public static void main(String[] args) {
        // String source = "1x^3 - 3*x*y"; //NUMBER VARIABLE POW NUMBER MINUS NUMBER STAR VARIABLE STAR VARIABLE
        String source = "1*x_1^3 - 3*(x*y)/3 + 4";
        MathLexer lexer = new MathLexer(source);
        List<Token> tokens = lexer.scanTokens();
        MathParser parser = new MathParser(tokens);
        Expr expression = parser.parse();
        System.out.println(expression);
        // IFunc func = expression.toFunction();
    }

    private static class ParseError extends RuntimeException {
        ParseError(Token token, String message) {
            super(message + " at " + token);
        }
    }
    
    private final List<Token> tokens;
    private int current = 0;
    
    MathParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return parseExpression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr parseExpression() {
        Expr expr = parseTerm();
        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = parseTerm();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr parseTerm() {
        Expr expr = parseFactor();
        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = parseFactor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr parseFactor() {
        Expr expr = parseExponent();
        while (match(FACTORIAL, POW)) {
            Token operator = previous();
            Expr right = parseExponent();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr parseExponent() {
        Expr expr = parseUnary();
        while (match(POW)) {
            Token operator = previous();
            Expr right = parseUnary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr parseUnary() {
        //TODO: Implement postfix unary operators (e.g. factorial)
        if (match(MINUS)) {
            Token operator = previous();
            Expr right = parseUnary();
            return new Expr.PrefixUnary(operator, right);
        }
        return parsePrimary();
    }

    private Expr parsePrimary() {
        if (match(NUMBER)) {
            return new Expr.Literal(previous().literal);
        } else if (match(VARIABLE)) {
            return new Expr.Variable(previous());
        } else
        if (match(LEFT_PARENTHESIS)) {
            Expr expr = parseExpression();
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
        return new ParseError(token, message);
    }

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
        return peek().type == END_OF_STATEMENT;
    }
    private Token peek() {
        return tokens.get(current);
    }
    private Token previous() {
        return tokens.get(current - 1);
    }
}
