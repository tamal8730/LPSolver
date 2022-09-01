import core.*;
import simplexlpsolver.SimplexLPSolver;

public class Main{
    public static void main(String[] args) {
        LPSolver solver = new SimplexLPSolver();

        LPConstraint c1 = new LPConstraint(new LPTerm[] { new LPTerm("x"), new LPTerm("y"), }, 12);
        LPConstraint c2 = new LPConstraint(new LPTerm[] { new LPTerm(2, "x"), new LPTerm("y"), }, 16);

        solver.addConstraint(c1);
        solver.addConstraint(c2);

        LPResult result = solver.maximize(new LPObjective(new LPTerm[] {
                new LPTerm(40, "x"),
                new LPTerm(30, "y"),
        }));

        System.out.println(result);
    }
}