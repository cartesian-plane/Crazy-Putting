package input;

import java.util.ArrayList;
import java.util.HashMap;

import static input.TokenType.*;

public class MathLexer {

    public static void main(String[] args) {
        String source = "ln(2, 0.5) ";
        MathLexer lexer = new MathLexer(source);
        ArrayList<Token> tokens = lexer.scanTokens();
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    private static final HashMap<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("log", LOG);
        keywords.put("root", ROOT);
        keywords.put("ln", LN);
        keywords.put("exp", EXP);
        keywords.put("abs", ABS);
        keywords.put("pow", POW);
        keywords.put("factorial", FACTORIAL);
        keywords.put("sin", SINE);
        keywords.put("cos", COSINE);
        keywords.put("tan", TANGENT);
        keywords.put("sec", SECANT);
        keywords.put("csc", COSECANT);
        keywords.put("cot", COTANGENT);
    }

    private final String source;
    private final ArrayList<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;

    public MathLexer(String source) {
        this.source = source;
    }

    // > scan-tokens
    ArrayList<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(END_OF_STATEMENT, "", null));
        return tokens;
    }
    private void scanToken() {
        char c = advance();
        switch (c) {
            // single character tokens
            case '(':
                addToken(LEFT_PARENTHESIS);
                break;
            case ')':
                addToken(RIGHT_PARENTHESIS);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case '*':
                addToken(STAR);
                break;
            case '/':
                addToken(SLASH);
                break;
            case '!':
                addToken(FACTORIAL);
                break;

            // ignore whitespace
            case ' ':
            case '\r':
            case '\t':
            case '\n':
            case ',': // ignore commas
                break;

            default:
              if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    throw new IllegalArgumentException("Unexpected Character");
                }
                    break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();
        String text = source.substring(start, current);
        // See if the identifier is a reserved word.
        TokenType type = keywords.get(text);
        // If it's not, it's a variable.
        if (type == null)
            type = VARIABLE;
        addToken(type);
    }

    private void number() {
        while (isDigit(peek()))
            advance();
        
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
            while (isDigit(peek()))
                advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
            (c >= 'A' && c <= 'Z') ||
            c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal));
    }
}