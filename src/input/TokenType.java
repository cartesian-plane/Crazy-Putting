package input;

public enum TokenType
{
    //single character tokens
    LEFT_PARENTHESIS, RIGHT_PARENTHESIS,
    
    //unary operators
    SINE, COSINE, TANGENT, SECANT, COSECANT, COTANGENT,
    SQRT, LN, EXP, ABS, POW, FACTORIAL,
    
    //binary operators
    SUBTRACTION, ADDITION, MULTIPLICATION, DIVISION,
    LOG, ROOT,

    //objects
    NUMBER, VARIABLE,

    //eos
    END_OF_STATEMENT
}
