package interfaces;

import java.util.ArrayList;

public class ODESystemTestFactory {
    ODESystem system(ArrayList<Number> initialStateVector, ArrayList<IFunc<Number, Number>> functions) {
        return new ODESystem(initialStateVector, functions);
    }

    public static void main(String[] args) {
        ArrayList<Number> initialStateVector = new ArrayList<>();
        ArrayList<IFunc<Number, Number>> functions = new ArrayList<>();

        Number[] vars = {1, 2, 3};

        initialStateVector.add(vars[0]);
        initialStateVector.add(vars[1]);
        initialStateVector.add(vars[2]);

        functions.add( (varss) -> {return varss[1]; });
        functions.add( (varss) -> {return varss[2]; });
        functions.add( (varss) -> {return - varss[0].doubleValue() - varss[2].doubleValue(); });

        ODESystemTestFactory factory = new ODESystemTestFactory();
        ODESystem syst = factory.system(initialStateVector, functions);

        System.out.println(syst + "\n" + syst.derivative());
    }
}
