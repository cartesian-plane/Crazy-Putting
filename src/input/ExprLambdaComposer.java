package input;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import interfaces.IFunc;

public class ExprLambdaComposer implements Expr.Visitor<IFunc<Number, Number>> {

    private final HashSet<String> vars;
    private final HashMap<String, Integer> varOrder;
    private final IFunc<Number, Number> lambda;

    public static void main(String[] args) {
        String source = "x' = x - 2*x*y";
        MathLexer lexer = new MathLexer(source);
        MathParser parser = new MathParser(lexer.getTokens());
        String lambdaVar = lexer.getLambdaVar();
        HashSet<String> vars = lexer.getVariables();

        Expr expression = parser.getExpression();
        
        System.out.println(lexer.getVariables());

        HashMap<String, Integer> varOrder = new HashMap<>();
        int i = 0;
        for(String var : lexer.getVariables()) {
            varOrder.put(var, i++);
        }

        ArrayList<Number> values = new ArrayList<Number>(){{add(2.0); add(3.0); add (5.0); add (7.0);}};
        
        ExprLambdaComposer composer = new ExprLambdaComposer(expression, lexer.getVariables(), varOrder);
        System.out.println(composer.lambda.apply(values));
        //System.out.println(lambda.apply(new ArrayList<Number>(){{add(1.0); add(2.0);}}));

    }

    public ExprLambdaComposer(Expr expr, HashSet<String> vars, HashMap<String, Integer> varOrder) {
        this.vars = vars;
        this.varOrder = varOrder;
        this.lambda = compose(expr);
    }

    private static class CompositionError extends RuntimeException {
        CompositionError(String message) {
            super(message);
        }
    }

    public IFunc<Number, Number> compose(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public IFunc<Number, Number> visitBinaryExpr(Expr.Binary expr) {
        switch (expr.operator.type) {
            case PLUS:
                return new IFunc<Number, Number>() {
                    @Override
                    public Number apply(ArrayList<Number> t) {
                        return expr.left.accept(ExprLambdaComposer.this).apply(t).doubleValue() + expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue();
                    }
                };
            case MINUS:
                return new IFunc<Number, Number>() {
                    @Override
                    public Number apply(ArrayList<Number> t) {
                        return expr.left.accept(ExprLambdaComposer.this).apply(t).doubleValue() - expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue();
                    }
                };
            case STAR:
                return new IFunc<Number, Number>() {
                    @Override
                    public Number apply(ArrayList<Number> t) {
                        return expr.left.accept(ExprLambdaComposer.this).apply(t).doubleValue() * expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue();
                    }
                };
            case SLASH:
                return new IFunc<Number, Number>() {
                    @Override
                    public Number apply(ArrayList<Number> t) {
                        return expr.left.accept(ExprLambdaComposer.this).apply(t).doubleValue() / expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue();
                    }
                };                
            default:
                throw new CompositionError("Invalid binary operator");
        }
    }

    @Override
    public IFunc<Number, Number> visitGroupingExpr(Expr.Grouping expr) {
        return new IFunc<Number, Number>() {
            @Override
            public Number apply(ArrayList<Number> t) {
                return expr.expression.accept(ExprLambdaComposer.this).apply(t);
            }
        };
    }

    @Override
    public IFunc<Number, Number> visitLiteralExpr(Expr.Literal expr) {
        return new IFunc<Number, Number>() {
            @Override
            public Number apply(ArrayList<Number> t) {
                return (Number)expr.value;
            }
        };
    }

    @Override
    public IFunc<Number, Number> visitVariableExpr(Expr.Variable expr) {
        if (expr.var.lexeme == null) throw new CompositionError("Variable name is null");
        return new IFunc<Number, Number>() {
            @Override
            public Number apply(ArrayList<Number> t) {
                return t.get(varOrder.get(expr.var.lexeme));
            }
        };
    }

    @Override
    public IFunc<Number, Number> visitPrefixUnaryExpr(Expr.PrefixUnary expr) {
        switch(expr.operator.type) {
            case MINUS:
                return new IFunc<Number, Number>() {
                    @Override
                    public Number apply(ArrayList<Number> t) {
                        return -expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue();
                    }
                };
            default:
                throw new CompositionError("Invalid unary operator");
        }
    }

    @Override
    public IFunc<Number, Number> visitPostfixUnaryExpr(Expr.PostfixUnary expr) {
        switch(expr.operator.type) {
            case FACTORIAL:
                return new IFunc<Number, Number>() {
                    @Override
                    public Number apply(ArrayList<Number> t) {
                        return null; // factorial(expr.left.accept(ExprLambdaComposer.this).apply(t).intValue()); //TODO: implement
                    }
                };
            default:
                throw new CompositionError("Invalid unary operator");
        }
    }    
}
