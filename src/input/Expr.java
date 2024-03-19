package input;

/**
 * Represents an expression in the form of a syntax tree.
 * 
 */
abstract class Expr {

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
        public String toString() {
            return "(" + left.toString() + " " + operator.type + ")";
        }
    }
}
