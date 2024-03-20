package input.tokens;

public enum TokenType
{
    //single character tokens
    LEFT_PARENTHESIS, RIGHT_PARENTHESIS,
    
    //unary operators
    SINE, COSINE, TANGENT, SECANT, COSECANT, COTANGENT,
    SQRT, LN, EXP, ABS, POW, FACTORIAL,

    //deriv
    DERIV, EQUALS,
    
    //binary operators
    MINUS, PLUS, STAR, SLASH,
    LOG, ROOT,

    //objects
    NUMBER, VARIABLE,

    //eos
    END_OF_STATEMENT
}
