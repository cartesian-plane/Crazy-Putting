package org.ken22.odesolver_p1.odeinput.tokens;

public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object literal;

    public Token(TokenType type, String lexeme, Object literal){
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
    }

    @Override
    public String toString(){
        return "[Type: "+type+", Lexeme: "+lexeme+", Literal: "+literal+"]";
    }

    @Override
    public boolean equals(Object obj){
        if (obj == this) return true;
        if (!(obj instanceof Token)) return false;
        Token token = (Token) obj;
        return token.type == type && token.lexeme.equals(lexeme) && token.literal.equals(literal);
    }
}
