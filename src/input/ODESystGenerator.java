package input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import interfaces.IFunc;

public class ODESystGenerator {

    private final ArrayList<String> lambdaVars; // The variables for which the lambda functions will be used
    private final HashSet<String> vars; // The full set of variables
    private final HashSet<String> vecVars; // The state vector variables
    private final HashMap<String, Integer> varOrder; // The order of the variables
    private final ArrayList<Expr> expressions; // The expressions of the derivatives
    private final ArrayList<IFunc> lambdas; // The lambda functions

    public static void main(String[] args) {
        String expression1 = "x2' = x1 + x2";
        String expression2 = "x1' = x2 + x3";
        String expression3 = "x3'' = -x1 - 2*x2' - x3";
        ODESystGenerator generator = new ODESystGenerator(expression1, expression2, expression3);
    }

    public ODESystGenerator(String... sources) {
        // Lex and parse the sources
        ArrayList<MathLexer> lexers = new ArrayList<>();
        ArrayList<MathParser> parsers = new ArrayList<>();
        for (String expression : sources) {
            lexers.add(new MathLexer(expression));
            parsers.add(new MathParser(lexers.getLast().getTokens()));
        }

        // Get the lambdaVars
        this.lambdaVars = lambdaVars(lexers);
        // Get the sources
        this.expressions = expressions(parsers);
        // Find the full set of variables
        this.vars = vars(lexers);
        // Find the state vector variables (i.e. the variables that are derivatives of other variables)
        this.vecVars = removeLastDerivatives((HashSet<String>)vars.clone());
        // Assign an arbitrary order to the variables
            // There are other ways of doing this, e.g. by using a predefined order on the variable names - then we wouldn't need to store the order as well, but it would be less flexible, and I don't want my colleagues to have to know such implementation details.
        this.varOrder = varOrder(vecVars);
        printInit();
        //Compose the lambda functions
        this.lambdas = lambdas(expressions, vecVars, varOrder);
    }

    private ArrayList<IFunc> lambdas(ArrayList<Expr> expressions, HashSet<String> vecVars, HashMap<String, Integer> varOrder) {
        return null;
    }

    private ArrayList<Expr> expressions(ArrayList<MathParser> parsers) {
        ArrayList<Expr> expressions = new ArrayList<>();
        for (MathParser parser : parsers) {
            expressions.add(parser.getExpression());
        }
        return expressions;
    }

    private HashMap<String, Integer> varOrder(HashSet<String> vecVars) {
        HashMap<String, Integer> varOrder = new HashMap<>();
        int i = 0;
        for (String var : vecVars) {
            varOrder.put(var, i++);
        }
        return varOrder;
    }

    private HashSet<String> vars(ArrayList<MathLexer> lexers) {
        HashSet<String> vars = new HashSet<>();
        for (MathLexer lexer : lexers) {
            vars.addAll(lexer.getVariables());
        }
        return vars;
    }

    private ArrayList<String> lambdaVars(ArrayList<MathLexer> lexers) {
        ArrayList<String> lambdaVars = new ArrayList<>();
        for (MathLexer lexer : lexers) {
            lambdaVars.add(lexer.getLambdaVar());
        }
        return lambdaVars;
    }

    /**
     * Really, really, really inefficient method to exclude the highest order derivatives from the hashset. But it works.
     * But seriously, this is a really bad way to do this.wtf.
     */
    private HashSet<String> removeLastDerivatives(HashSet<String> vars) {
        HashSet<String> temp = new HashSet<>();
        for(String var : vars) {
            boolean isHighestDerivative = true;
            // if var is a derivative
            if (var.contains("'")) {
                isHighestDerivative = false;
                //if there exists a higher order derivative of var
                for(String der : vars) {
                    if (var.equals(der)) continue;
                    if (isDerivativeOf(var, der)) {
                            isHighestDerivative = true;
                        }
                }
            }
            if (!isHighestDerivative) temp.add(var);
        }
        vars.removeAll(temp);
        return vars;
    }

    private boolean isDerivativeOf(String var, String der) {
        return der.startsWith(var) && der.substring(var.length()).chars().allMatch(c -> c == '\'');
    }

    private void printInit() {
        System.out.println("lambdaVars: " + lambdaVars + "; expressions:");
        for (Expr expression : this.expressions) {
            System.out.println(expression);
        }
        System.out.println("vars: " + vars);
        System.out.println("vecVars: " + vecVars);
        System.out.println("varOrder: " + varOrder);
    }
}
