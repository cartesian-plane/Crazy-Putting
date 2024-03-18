package input;

public class Token {
    public final TokenType type;
    public final String lexeme;

    public Token(String lexeme, TokenType type){
        this.lexeme = lexeme;
        this.type = type;
    }

    public String toString(){
        return "[Lexeme: " + lexeme + "Type: " + type + "]";
    }
}
