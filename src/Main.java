import core.*;
import simplexlpsolver.SimplexLPSolver;

public class Main{
    public static void main(String[] args) {
        LPSolver solver = new SimplexLPSolver();

        LPConstraint c1 = new LPConstraint(new LPTerm[] { new LPTerm("X1"), new LPTerm("X2"), }, 12);
        LPConstraint c2 = new LPConstraint(new LPTerm[] { new LPTerm(2, "X1"), new LPTerm("X2"), }, 16);

        solver.addConstraint(c1);
        solver.addConstraint(c2);

        LPResult result = solver.maximize(new LPObjective(new LPTerm[] {
                new LPTerm(40, "X1"),
                new LPTerm(30, "X2"),
        }));

        System.out.println(result);
    }
}