# LPSolver
A Java implementation of Simplex Method for solving Linear Programming Problems

## About
This implementation of the [Simplex Algorithm](https://en.wikipedia.org/wiki/Simplex_algorithm) 
only supports [Linear Programs](https://en.wikipedia.org/wiki/Linear_programming) of
the form:

    Maximize a1*x1 + a2*x2 +...+ an*xn

    Subject to,
    c11*x1 + c12*x2 +...+ c1n*xn <= b1
    c21*x1 + c22*x2 +...+ c2n*xn <= b2
                    .
                    .
                    .
    cm1*x1 + cm2*x2 +...+ cmn*xn <= bm
    
    a1,a2,...an, c11,c12,...,c21,c22,...,cmn>=0

## Example
Let us consider the following problem:

    Maximize 40x + 30y

    Subject to,
    x + y <= 12
    2x + y <= 16
    x>=0, y>=0

We first create an object of type [`LPSolver`](src/core/LPSolver.java)

    LPSolver solver = new SimplexLPSolver();

Then we create the objective function
    
    LPObjective objective = new LPObjective(new LPTerm[] {
            new LPTerm(40, "x"),
            new LPTerm(30, "y"),
    });

Then we create the constraints (the non-negativity constraints are implied and need not be added separately)

    LPConstraint c1 = new LPConstraint(new LPTerm[] { new LPTerm("x"), new LPTerm("y"), }, 12);
    LPConstraint c2 = new LPConstraint(new LPTerm[] { new LPTerm(2, "x"), new LPTerm("y"), }, 16);

Now we add the constraints to the solver

    solver.addConstraint(c1);
    solver.addConstraint(c2);

We finally maximize the objective function

    LPResult result = solver.maximize(objective);

**Complete example**
```
    import core.*;
    import simplexlpsolver.SimplexLPSolver;
    
    public class Main{
        public static void main(String[] args) {
        
            LPSolver solver = new SimplexLPSolver();
            
            LPObjective objective = new LPObjective(new LPTerm[] {
                new LPTerm(40, "x"),
                new LPTerm(30, "y"),
            });
    
            LPConstraint c1 = new LPConstraint(new LPTerm[] { new LPTerm("x"), new LPTerm("y"), }, 12);
            LPConstraint c2 = new LPConstraint(new LPTerm[] { new LPTerm(2, "x"), new LPTerm("y"), }, 16);
    
            solver.addConstraint(c1);
            solver.addConstraint(c2);
    
            LPResult result = solver.maximize(objective);
    
            System.out.println(result);
        }
    }
```

**Output**

    {
	    optimum_variable_values: {
		    {y = 8.0}
		    {x = 4.0}
	}
	    optimum_obj_func_value: 400.0
    }

## References
https://www.hec.ca/en/cams/help/topics/The_steps_of_the_simplex_algorithm.pdf