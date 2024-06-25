package org.ken22.odesolver_p1.odeinput;

import org.ken22.odesolver_p1.odeinput.expressions.Expr;
import org.ken22.odesolver_p1.odeinput.expressions.ExprPrinter;
import org.ken22.odesolver_p1.odeinput.tokens.Token;
import org.ken22.odesolver_p1.odeinput.tokens.TokenType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MathParser {

    private final List<Token> tokens;
    private final String lambdaVar; //unused
    private final ArrayList<String> exprVars;
    private final HashSet<String> vars;
    private final Expr expression;
    private int current = 0;

    public static void main(String[] args) {
        // String source = "1x^3 - 3*x*y"; //NUMBER VARIABLE POW NUMBER MINUS NUMBER STAR VARIABLE STAR VARIABLE
        String source = "x'' = 1*x_1^3 - 3*(x*y)/3 + 4";

        MathLexer lexer = new MathLexer(source);
        List<Token> tokens = lexer.getTokens();
        System.out.println(lexer.getVariables());

        MathParser parser = new MathParser(tokens);
        Expr expression = parser.getExpression();
        System.out.println(expression);
        ExprPrinter printer = new ExprPrinter();
        System.out.println(printer.print(expression));
        System.out.println(parser.getVars());
    }

    private static class ParseError extends RuntimeException {
        ParseError(Token token, String message) {
            super(message + " at " + token);
        }
    }

    /**
     * This expects a list of tokens of the form [VARIABLE, EQUALS, ...], where the variable is the lambdaVar.
     * The lambdaVar is the variable for which the lambda function will be used.
     * WARNING: This means that the user will have to input equations of the form <highest derivative of one variable> = <expression of the other derivatives>.
     * @param tokens
     */
    public MathParser(List<Token> tokens) {
        this.tokens = tokens;
        this.lambdaVar = tokens.get(0).lexeme;
        tokens.remove(0);
        tokens.remove(0);
        this.exprVars = findExprVars();
        this.expression = parse();
        this.vars = findVariables();
    }

    private ArrayList<String> findExprVars() {
        ArrayList<String> exprVars = new ArrayList<>();
        for (Token token : tokens) {
            if (token.type == TokenType.VARIABLE) {
                exprVars.add(token.lexeme);
            }
        }
        return exprVars;
    }

    private HashSet<String> findVariables() {
        HashSet<String> vars = new HashSet<>();
        for (Token token : tokens) {
            if (token.type == TokenType.VARIABLE) {
                vars.add(token.lexeme);
            }
        }
        return vars;
    }

    private Expr parse() {
        try {
            return parseExpression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr parseExpression() {
        Expr expr = parseTerm();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = parseTerm();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr parseTerm() {
        Expr expr = parseFactor();
        while (match(TokenType.SLASH, TokenType.STAR)) {
            Token operator = previous();
            Expr right = parseFactor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr parseFactor() {
        Expr expr = parseExponent();
        while (match(TokenType.FACTORIAL, TokenType.POW)) {
            Token operator = previous();
            Expr right = parseExponent();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr parseExponent() {
        Expr expr = parseUnary();
        while (match(TokenType.POW)) {
            Token operator = previous();
            Expr right = parseUnary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr parseUnary() {
        //TODO: Implement postfix unary operators (e.g. factorial)
        if (match(TokenType.MINUS)) {
            Token operator = previous();
            Expr right = parseUnary();
            return new Expr.PrefixUnary(operator, right);
        }
        return parsePrimary();
    }

    private Expr parsePrimary() {
        if (match(TokenType.NUMBER)) {
            return new Expr.Literal(previous().literal);
        } else if (match(TokenType.VARIABLE)) {
            return new Expr.Variable(previous());
        } else
        if (match(TokenType.LEFT_PARENTHESIS)) {
            Expr expr = parseExpression();
            consume(TokenType.RIGHT_PARENTHESIS, "Expect ')' after expression.");
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
        return peek().type == TokenType.END_OF_STATEMENT;
    }
    private Token peek() {
        return tokens.get(current);
    }
    private Token previous() {
        return tokens.get(current - 1);
    }

    public Expr getExpression() {
        return expression;
    }

    public HashSet<String> getVars() {
        return vars;
    }

    public ArrayList<String> getExprVars() {
        return exprVars;
    }
}
