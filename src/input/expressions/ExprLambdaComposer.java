package input.expressions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import input.MathLexer;
import input.MathParser;
import interfaces.IFunc;

public class ExprLambdaComposer implements Expr.Visitor<IFunc<Double, Double>> {

    private final HashSet<String> vecVars; 
    private final HashMap<String, Integer> varOrder;
    private final IFunc<Double, Double> lambda;

    public static void main(String[] args) {
        // String source = "x' = x - 2*x*y";
        String source = "x'' = 1*x_1^2 - 3*(x*y)/2 + 4";
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

        ArrayList<Double> values = new ArrayList<Double>(){{add(1.0); add(2.0); add (3.0); add (4.0);}};
        
        ExprLambdaComposer composer = new ExprLambdaComposer(expression, lexer.getVariables(), varOrder);
        System.out.println(composer.lambda.apply(values));
        //System.out.println(lambda.apply(new ArrayList<Double>(){{add(1.0); add(2.0);}}));

    }

    public ExprLambdaComposer(Expr expr, HashSet<String> vecVars, HashMap<String, Integer> varOrder) {
        this.vecVars = vecVars;
        this.varOrder = varOrder;
        this.lambda = compose(expr);
    }

    private static class CompositionError extends RuntimeException {
        CompositionError(String message) {
            super(message);
        }
    }

    public IFunc<Double, Double> compose(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public IFunc<Double, Double> visitBinaryExpr(Expr.Binary expr) {
        switch (expr.operator.type) {
            case PLUS:
                return new IFunc<Double, Double>() {
                    @Override
                    public Double apply(ArrayList<Double> t) {
                        return expr.left.accept(ExprLambdaComposer.this).apply(t).doubleValue() + expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue();
                    }
                };
            case MINUS:
                return new IFunc<Double, Double>() {
                    @Override
                    public Double apply(ArrayList<Double> t) {
                        return expr.left.accept(ExprLambdaComposer.this).apply(t).doubleValue() - expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue();
                    }
                };
            case STAR:
                return new IFunc<Double, Double>() {
                    @Override
                    public Double apply(ArrayList<Double> t) {
                        return expr.left.accept(ExprLambdaComposer.this).apply(t).doubleValue() * expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue();
                    }
                };
            case SLASH:
                return new IFunc<Double, Double>() {
                    @Override
                    public Double apply(ArrayList<Double> t) {
                        return expr.left.accept(ExprLambdaComposer.this).apply(t).doubleValue() / expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue();
                    }
                };
            case POW:
                return new IFunc<Double, Double>() {
                    @Override
                    public Double apply(ArrayList<Double> t) {
                        return Math.pow(expr.left.accept(ExprLambdaComposer.this).apply(t).doubleValue(), expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue());
                    }
                };
            default:
                throw new CompositionError("Invalid binary operator");
        }
    }

    @Override
    public IFunc<Double, Double> visitGroupingExpr(Expr.Grouping expr) {
        return new IFunc<Double, Double>() {
            @Override
            public Double apply(ArrayList<Double> t) {
                return expr.expression.accept(ExprLambdaComposer.this).apply(t);
            }
        };
    }

    @Override
    public IFunc<Double, Double> visitLiteralExpr(Expr.Literal expr) {
        return new IFunc<Double, Double>() {
            @Override
            public Double apply(ArrayList<Double> t) {
                return (Double)expr.value;
            }
        };
    }

    @Override
    public IFunc<Double, Double> visitVariableExpr(Expr.Variable expr) {
        if (expr.var.lexeme == null) throw new CompositionError("Variable name is null");
        return new IFunc<Double, Double>() {
            @Override
            public Double apply(ArrayList<Double> t) {
                return t.get(varOrder.get(expr.var.lexeme));
            }
        };
    }

    @Override
    public IFunc<Double, Double> visitPrefixUnaryExpr(Expr.PrefixUnary expr) {
        switch(expr.operator.type) {
            case MINUS:
                return new IFunc<Double, Double>() {
                    @Override
                    public Double apply(ArrayList<Double> t) {
                        return -expr.right.accept(ExprLambdaComposer.this).apply(t).doubleValue();
                    }
                };
            default:
                throw new CompositionError("Invalid unary operator");
        }
    }

    @Override
    public IFunc<Double, Double> visitPostfixUnaryExpr(Expr.PostfixUnary expr) {
        switch(expr.operator.type) {
            case FACTORIAL:
                return new IFunc<Double, Double>() {
                    @Override
                    public Double apply(ArrayList<Double> t) {
                        return null; // factorial(expr.left.accept(ExprLambdaComposer.this).apply(t).intValue()); //TODO: implement
                    }
                };
            default:
                throw new CompositionError("Invalid unary operator");
        }
    }

    public IFunc<Double, Double> getLambda() {
        return lambda;
    }
}
