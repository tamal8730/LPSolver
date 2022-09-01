package core;

/**
 * Linear Programming Problem solver class
 */
public interface LPSolver {

    /**
     * Adds a constraint of the form (c1*x1)+(c2*x2)+...+(cn*xn) &lt;=b to the problem
     */
    void addConstraint(LPConstraint constraint);

    /**
     * Maximizes the specified objective function
     * 
     * @param objective The objective function to maximize
     * @return The maximum value of the objective function, and value of the
     *         variables that yields that maximum, encapsulated in an
     *         {@link LPResult LRResult}
     */
    LPResult maximize(LPObjective objective);
}
