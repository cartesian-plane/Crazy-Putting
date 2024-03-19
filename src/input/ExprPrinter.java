package input;

public class ExprPrinter implements Expr.Visitor<String> {

    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        if (expr.var.lexeme == null) return "nil";
        return expr.var.lexeme.toString();
    }

    @Override
    public String visitPrefixUnaryExpr(Expr.PrefixUnary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitPostfixUnaryExpr(Expr.PostfixUnary expr) {
        return parenthesize(expr.operator.lexeme, expr.left);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }       
}
