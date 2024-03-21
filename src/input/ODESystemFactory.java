package input;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import input.expressions.Expr;
import input.expressions.ExprLambdaComposer;
import interfaces.IFunc;
import interfaces.ODESystem;

public class ODESystemFactory {

    private final ArrayList<String> lambdaVars; // The variables for which the lambda functions will be used
    private final HashSet<String> vars; // The full set of variables
    private final HashSet<String> vecVars; // The state vector variables
    private final HashMap<Integer, String> reverseVarOrder = new HashMap<>();
    private final HashMap<String, Integer> varOrder; // The order of the variables
    private final ArrayList<Expr> expressions; // The expressions of the derivatives
    private final ArrayList<IFunc<Double, Double>> lambdas; // The lambda functions
    private final ODESystem system; // The system of ODEs

    // let's hope to god it works
    public static void main(String[] args) {
        // TODO: In this example below, some equations are stated in terms of the derivatives of other equations. Does this always work with this code?
        ArrayList<String> ex = new ArrayList<>(){
            {
                add("x2' = x1 + x2");
                add("x1' = x2 + x3");
                add("x3' = -x1'");
                add("x3'' = -x1 - 2*x2' - x3");
            }
        };
        HashMap<String, Double> initialState = new HashMap<>(){
            {
                put("x1", 1.0);
                put("x2", 2.0);
                put("x3", 3.0);
                put("x1'", 4.0);
                put("x2'", 5.0);
            }
        };

        // String ex1 = "y' = 1";
        // String ex2 = "x' = 2";
        // HashMap<String, Double> initialState = new HashMap<>() {
        //     {
        //         put("x", 1.0);
        //         put("y", 2.0);
        //     }
        // };

        ODESystemFactory generator = new ODESystemFactory(initialState, ex);
    }

    public ODESystemFactory(HashMap<String, Double> initialState, ArrayList<String> sources) {
        // Lex and parse the sources
        ArrayList<MathLexer> lexers = new ArrayList<>();
        ArrayList<MathParser> parsers = new ArrayList<>();
        for (String expression : sources) {
            lexers.add(new MathLexer(expression));
            parsers.add(new MathParser(lexers.getLast().getTokens()));
            System.out.println(parsers.getLast().getExprVars());
        }

        this.lambdaVars = lambdaVars(lexers);
        this.expressions = expressions(parsers);
        this.vars = vars(lexers);
        this.vecVars = generateVecVars(vars, lexers, parsers);
        // Assign an arbitrary order to the variables
            // There are other ways of doing this, e.g. by using a predefined order on the variable names - then we wouldn't need to store the order as well, but it would be less flexible, and I don't want my colleagues to have to know such implementation details.
        this.varOrder = varOrder(vecVars);
        printInit();
        //Compose the lambda functions
        this.lambdas = lambdas(expressions, vecVars, varOrder);
        // Create the functions for the system
        ArrayList<IFunc<Double, Double>> functions = createFunctions(lambdas, lambdaVars, vecVars, varOrder);
        // Create the initial state vector
        ArrayList<Double> initialStateVector = createInitialStateVector(initialState, vecVars, reverseVarOrder);
        System.out.println("initialStateVector: " + initialStateVector);
        // Create the ODESystem  
        this.system = new ODESystem(initialStateVector, functions);
    }

    private ArrayList<Double> createInitialStateVector(HashMap<String, Double> initialState, HashSet<String> vecVars, HashMap<Integer, String> reverseVarOrder) {
        ArrayList<Double> initialStateVector = new ArrayList<>();
        for (int i = 0; i < vecVars.size(); i++) {
            initialStateVector.add(initialState.get(reverseVarOrder.get(i)));
        }
        return initialStateVector;
    }

    private HashSet<String> generateVecVars(HashSet<String> vars, ArrayList<MathLexer> lexers, ArrayList<MathParser> parsers) {
        HashSet<String> vecVars = (HashSet<String>)vars.clone();
        for(MathLexer lexer : lexers)
            vecVars.remove(lexer.getLambdaVar()+"\'");
        //System.out.println(vecVars);
        for (MathParser parser : parsers) {
            vecVars.addAll(parser.getExprVars());
        }
        //System.out.println(vecVars);
        return vecVars;
    }

    private ArrayList<IFunc<Double, Double>> createFunctions(ArrayList<IFunc<Double, Double>> lambdas, ArrayList<String> lambdaVars, HashSet<String> vecVars,
            HashMap<String, Integer> varOrder) {
        ArrayList<IFunc<Double, Double>> functions = new ArrayList<>();
        for (String vecVar : vecVars) {
            boolean exists = false;
            for(int i = 0; i < lambdaVars.size(); i++) {
                if (vecVar.equals(lambdaVars.get(i))) {
                    exists = true;
                    functions.add(lambdas.get(i));
                }
            }

            // if the var doesn't have a lambda function, we create a lambda function that returns the variable whose derivative it is
            if (!exists) {
                functions.add(new IFunc<Double, Double>() {
                    @Override
                    public Double apply(ArrayList<Double> t) {
                        return t.get(findAntiderivative(vecVar, vecVars, varOrder));
                    }
                });
            }
        }
        return functions;
    }

    private int findAntiderivative(String vecVar, HashSet<String> vecVars, HashMap<String, Integer> varOrder) {
        if (!vecVar.endsWith("'"))
            throw new IllegalArgumentException("The variable must be a derivative.");
        
        String antiderivative = vecVar.substring(0, vecVar.length() - 1);
        for (String var : vecVars) {
            if (var.equals(antiderivative))
                return varOrder.get(var);
        }
        throw new IllegalArgumentException("Missing information about " + vecVar + " in the system.");
    }

    private ArrayList<IFunc<Double, Double>> lambdas(ArrayList<Expr> expressions, HashSet<String> vecVars, HashMap<String, Integer> varOrder) {
        ArrayList<IFunc<Double, Double>> lambdas = new ArrayList<>();
        for (Expr expression : expressions) {
            ExprLambdaComposer composer = new ExprLambdaComposer(expression, vecVars, varOrder);
            lambdas.add(composer.getLambda());
        }
        return lambdas;
    }

    private ArrayList<Expr> expressions(ArrayList<MathParser> parsers) {
        ArrayList<Expr> expressions = new ArrayList<>();
        for (MathParser parser : parsers) {
            expressions.add(parser.getExpression());
        }
        return expressions;
    }

    private HashMap<String, Integer> varOrder(HashSet<String> vecVars) {
        ArrayList<String> varList = new ArrayList<>(vecVars);
        for (String var : vecVars) {
            varList.add(var);
        }

        // eliminate duplicates
        HashSet<String> uniqueVars = new HashSet<>(varList);
        varList.clear();
        varList.addAll(uniqueVars);
        
        // sort the list lexicographically
            // This is a bit of a hack, but it works. It is because the variables are evaluated in the varOrder, to always compute the variables themselves before the derivatives
        Collections.sort(varList); 
        System.out.println("idk: "+ varList);

        

        // create the hashmap with the order of the variables
        HashMap<String, Integer> varOrder = new HashMap<>();
        int i = 0;
        for (String var : varList) {
            reverseVarOrder.put(i, var);
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
     * Moreover, it doesn't work! ffs.
     * This ought to be cut off, but I'm keeping it for you, to wonder at the stupidity behind it.
     */
    @Deprecated
    private HashSet<String> removeHighestDerivatives(HashSet<String> vars, ArrayList<MathParser> parsers) {
        //remove all highest order derivatives
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

        //add back the derivatives that appear in expressions
        parsers.getLast().getExprVars().forEach(vars::add); // I like this code, but it's hard to debug.
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

    public ODESystem getSyst() {
        return system;
    }
}
