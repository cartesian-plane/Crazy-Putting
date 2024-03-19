package input;

public class SyntaxTreeNode {

    public static class Addition extends SyntaxTreeNode {
        Addition(SyntaxTreeNode left, SyntaxTreeNode right) {
            this.left = left;
            this.right = right;
        }

        final SyntaxTreeNode left;
        final SyntaxTreeNode right;

        @Override
        public String toString() {
            return "(" + left.toString() + " + " + right.toString() + ")";
        }
    }

    public static class Subtraction extends SyntaxTreeNode {
        Subtraction(SyntaxTreeNode left, SyntaxTreeNode right) {
            this.left = left;
            this.right = right;
        }

        final SyntaxTreeNode left;
        final SyntaxTreeNode right;

        @Override
        public String toString() {
            return "(" + left.toString() + " - " + right.toString() + ")";
        }
    }

    public static class Multiplication extends SyntaxTreeNode {
        Multiplication(SyntaxTreeNode left, SyntaxTreeNode right) {
            this.left = left;
            this.right = right;
        }

        final SyntaxTreeNode left;
        final SyntaxTreeNode right;

        @Override
        public String toString() {
            return "(" + left.toString() + " * " + right.toString() + ")";
        }
    }

    public static class Division extends SyntaxTreeNode {
        Division(SyntaxTreeNode left, SyntaxTreeNode right) {
            this.left = left;
            this.right = right;
        }

        final SyntaxTreeNode left;
        final SyntaxTreeNode right;

        @Override
        public String toString() {
            return "(" + left.toString() + " / " + right.toString() + ")";
        }
    }

    public static class Exponentiation extends SyntaxTreeNode {
        Exponentiation(SyntaxTreeNode left, SyntaxTreeNode right) {
            this.left = left;
            this.right = right;
        }

        final SyntaxTreeNode left;
        final SyntaxTreeNode right;

        @Override
        public String toString() {
            return "(" + left.toString() + " ^ " + right.toString() + ")";
        }
    }

    public static class Negation extends SyntaxTreeNode {
        Negation(SyntaxTreeNode right) {
            this.right = right;
        }

        final SyntaxTreeNode right;

        @Override
        public String toString() {
            return "(-" + right.toString() + ")";
        }
    }

    public static class Factorial extends SyntaxTreeNode {
        Factorial(SyntaxTreeNode left) {
            this.left = left;
        }

        final SyntaxTreeNode left;

        @Override
        public String toString() {
            return "(" + left.toString() + "!)";
        }
    }

    public static class Literal extends SyntaxTreeNode {
        Literal(Object value) {
            this.value = value;
        }

        final Object value;

        @Override
        public String toString() {
            return value.toString();
        }
    }

    public static class Variable extends SyntaxTreeNode {
        Variable(String var) {
            this.var = var;
        }

        final String var;

        @Override
        public String toString() {
            return "(" + var + ")";
        }
    }
}
