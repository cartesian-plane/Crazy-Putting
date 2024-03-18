package input;

import java.util.HashMap;
import java.util.ArrayList;

import static input.TokenType.*;

/**
 * Scans the input string and breaks it down into tokens.
 */
public class MathLexer {
    private String source; 
    private ArrayList<Token> tokens = new ArrayList<Token>();

    public static void main(String[] args) {
        MathLexer lexer = new MathLexer("");
        lexer.scanTokens().forEach(System.out::println);
    }
    
    public MathLexer(String source){
        this.source = source;
    }

    private int start = 0; //token start offset
    private int current = 0; //source index of currently read char
    
    // private static final HashMap<String, TokenType> keywords = HashMap.ofEntries(
    //     new HashMap.SimpleEntry<String, TokenType>("and", CONJUNCTION),
    //     new HashMap.SimpleEntry<String, TokenType>("or", INCLUSIVE_DISJUNCTION),
    //     new HashMap.SimpleEntry<String, TokenType>("xor", EXCLUSIVE_DISJUNCTION),
    //     new HashMap.SimpleEntry<String, TokenType>("then", IMPLICATION),
    //     new HashMap.SimpleEntry<String, TokenType>("iff", BIDIRECTIONAL_IMPLICATION),
    //     new HashMap.SimpleEntry<String, TokenType>("true", TRUE),
    //     new HashMap.SimpleEntry<String, TokenType>("false", FALSE)
    // );

    public ArrayList<Token> scanTokens(){
        while(current < source.length()){
            start = current;
            char c = source.charAt(current++);
            switch(c){
                //single character tokens
                case ')': addToken(RIGHT_PARENTHESIS); break;
                case '(': addToken(LEFT_PARENTHESIS); break;
                case '+': addToken(ADDITION); break;
                case '*': addToken(MULTIPLICATION); break;
                case '/': addToken(DIVISION); break;
                case ';': addToken(END_OF_STATEMENT); break;
                
                //multicharacter tokens
                case '-':
                    if(source.charAt(current) == '>'){
                        addToken(IMPLICATION);
                        current++;
                    }
                    break;
                case '<': 
                    if(source.charAt(current) == '-')
                        if(current+1 < source.length() && source.charAt(current+1) == '>'){
                            addToken(IMPLICATION);
                            current+=2;
                        }
                    break;
                
                //ignore
                case ' ':  //whitespace
                case '\r': //carriage return
                case '\t': //tab
                    break;
                case '\n': //newline
                    line++;
                    break;
                
                default:
                    if(c >= 'a' && c <= 'z') //isAlpha
                        if (current >= source.length() || source.charAt(current) < 'a' || source.charAt(current) > 'z')
                            addToken(ATOMIC_PROPOSITION);
                        else {
                            while(current < source.length() && source.charAt(current) >= 'a' && source.charAt(current) <= 'z') current++;
                            String text = source.substring(start, current);
                            TokenType type = keywords.get(text);
                            if (type == null) Logic.error(line, "Unknown identifier '"+text+"'");
                            else addToken(type, text, null, line);
                        } 
                    else {
                        Logic.error(line, "Unexpected character '"+c+"'");
                    }
            }            
        }

        addToken(EOF);
        return tokens;
    }
    
    //number
    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

        while (isDigit(peek())) advance();
    }

    addToken(NUMBER,
        Double.parseDouble(source.substring(start, current)));
    }
    
    private void addToken(TokenType type) {
        tokens.add(new Token(null, type));
    }
    private void addToken(String text, TokenType type) {
        tokens.add(new Token(text, type));
    }
}