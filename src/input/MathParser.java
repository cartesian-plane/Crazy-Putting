package input;

import java.util.HashSet;
import java.util.List;

import interfaces.IFunc;

import static input.TokenType.*;

class MathParser {

    private final List<Token> tokens;
    private final String lambdaVar;
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
    MathParser(List<Token> tokens) {
        this.tokens = tokens;
        this.lambdaVar = tokens.get(0).lexeme;
        tokens.remove(0);
        tokens.remove(0);
        this.expression = parse();
        this.vars = findVariables();
    }

    private HashSet<String> findVariables() {
        HashSet<String> vars = new HashSet<>();
        for (Token token : tokens) {
            if (token.type == VARIABLE) {
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

    public Expr getExpression() {
        return expression;
    }

    public HashSet<String> getVars() {
        return vars;
    }
}
