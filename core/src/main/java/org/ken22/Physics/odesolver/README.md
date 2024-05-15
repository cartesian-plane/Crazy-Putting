<h1>How to call the solvers in code</h1>

1. Create a **Context**
 >ODESolver context = new ODESolver;

2. Set the **Context's** **Strategy**
> context.setStrategy(new EulerMethod(args))

You can use one of the systems from the **ODESystemTestFactory**
3. Call the
>context.solve()

method, and store the **ODESolution** object.

You can then use that object.