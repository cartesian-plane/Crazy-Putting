package input;

/**
 * Represents an expression in the form of a parse tree.
 */
abstract class Expr {

    //TODO:Replace the infix toString() methods with Visitor classes //currently we only have prefix toString() visitor

    interface Visitor<R> {
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitVariableExpr(Variable expr);
        R visitPrefixUnaryExpr(PrefixUnary expr);
        R visitPostfixUnaryExpr(PostfixUnary expr);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        final Expr left;
        final Token operator;
        final Expr right;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        @Override
        public String toString() {
            return "( " + left.toString() + " " + operator.type + " " + right.toString() + " )";
        }
    }

    static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        @Override
        public String toString() {
            return "( " + expression.toString() + " )";
        }

    }

    static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        }

        final Object value;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }

    static class Variable extends Expr {
        Variable(Token var) {
            this.var = var;
        }

        final Token var;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        @Override
        public String toString() {
            return "( " + var.lexeme + " )";
        }
    }

    static class PrefixUnary extends Expr {
        PrefixUnary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        final Token operator;
        final Expr right;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrefixUnaryExpr(this);
        }

        @Override
        public String toString() {
            return "(" + operator.type + " " + right.toString() + ")";
        }
    }

    static class PostfixUnary extends Expr {
        PostfixUnary(Expr left, Token operator) {
            this.left = left;
            this.operator = operator;
        }

        final Expr left;
        final Token operator;

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPostfixUnaryExpr(this);
        }

        @Override
        public String toString() {
            return "(" + left.toString() + " " + operator.type + ")";
        }
    }
}
