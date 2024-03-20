


public class SolverInput {
    private ArrayList<String> equations;
    private ArrayList<Double> initialValues;
    private double stepSize;
    private double time;
    private boolean graph;
    private boolean phaseSpace;
    private boolean table;
    private String solverType;

    // Constructor
    public SolverInput(ArrayList<String> equations, ArrayList<Double> initialValues, double stepSize, double time, boolean graph, boolean phaseSpace, boolean table, String solverType) {
        this.equations = equations;
        this.initialValues = initialValues;
        this.stepSize = stepSize;
        this.time = time;
        this.graph = graph;
        this.phaseSpace = phaseSpace;
        this.table = table;
        this.solverType = solverType;
    }

}
