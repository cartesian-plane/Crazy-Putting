package org.ken22.input.expressions;

import org.ken22.input.tokens.Token;

/**
 * Represents an expression in the form of a parse tree.
 */
public abstract class Expr {

    //TODO:Replace the infix toString() methods with Visitor classes //currently we only have prefix toString() visitor

    interface Visitor<R> {
        public R visitBinaryExpr(Binary expr);
        public R visitGroupingExpr(Grouping expr);
        public R visitLiteralExpr(Literal expr);
        public R visitVariableExpr(Variable expr);
        public R visitPrefixUnaryExpr(PrefixUnary expr);
        public R visitPostfixUnaryExpr(PostfixUnary expr);
    }

    abstract <R> R accept(Visitor<R> visitor);

    public static class Binary extends Expr {
        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        public final Expr left;
        public final Token operator;
        public final Expr right;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        @Override
        public String toString() {
            return "( " + left.toString() + " " + operator.type + " " + right.toString() + " )";
        }
    }

    public static class Grouping extends Expr {
        public Grouping(Expr expression) {
            this.expression = expression;
        }

        public final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        @Override
        public String toString() {
            return "( " + expression.toString() + " )";
        }

    }

    public static class Literal extends Expr {
        public Literal(Object value) {
            this.value = value;
        }

        public final Object value;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    public static class Variable extends Expr {
        public Variable(Token var) {
            this.var = var;
        }

        public final Token var;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        @Override
        public String toString() {
            return "( " + var.lexeme + " )";
        }
    }

    public static class PrefixUnary extends Expr {
        public PrefixUnary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        public final Token operator;
        public final Expr right;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrefixUnaryExpr(this);
        }

        @Override
        public String toString() {
            return "(" + operator.type + " " + right.toString() + ")";
        }
    }

    public static class PostfixUnary extends Expr {
        public PostfixUnary(Expr left, Token operator) {
            this.left = left;
            this.operator = operator;
        }

        public final Expr left;
        public final Token operator;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPostfixUnaryExpr(this);
        }

        @Override
        public String toString() {
            return "(" + left.toString() + " " + operator.type + ")";
        }
    }
}
