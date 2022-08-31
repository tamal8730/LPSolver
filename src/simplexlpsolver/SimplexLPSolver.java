package simplexlpsolver;

import core.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>
 * Implementation of an {@link LPSolver LPSolver}, that uses the Simplex
 * Algorithm to solve Linear Programming Problems.
 * </p>
 *
 * <p>
 * This implementation can only solve problems of the form:
 *
 * <pre>
 * Maximize (a1*x1)+(a2*x2)+...+(an*xn)
 *
 * Subject to,
 *
 *      (c11*x1)+(c12*x2)+...+(c1n*xn)<=b1
 *      (c21*x1)+(c22*x2)+...+(c2n*xn)<=b2
 *                              .
 *                              .
 *                              .
 *      (cn1*x1)+(cn2*x2)+...+(cnn*xn)<=bn
 *
 *      a1,a2,..an,c11,c12,..c1n,c21,..cnn>=0
 * </pre>
 * </p>
 */
public class SimplexLPSolver implements LPSolver {

    /**
     * Count of slack variables
     */
    private int slackVarCount = 0;

    /**
     * Count of all variables (including slack variables)
     */
    private int varCount = 0;

    /**
     * @return Count of all variables (including slack variables)
     */
    public int getTotalVariableCount() {
        return variablesSet.size();
    }

    /**
     * List of all constraints with added slack
     */
    private final List<LPConstraint> constraints = new ArrayList<>();

    /**
     * Set of labels of all the variables added
     */
    private final Set<String> variablesSet = new HashSet<>();


    /**
     * <p>
     * Variables gets added to the variablesSet as they come along in the constraints.
     * These variables can be uniquely identified by their indices in the simplex table columns.
     * <tt>indexVariableMap</tt> is a map that takes in the index of the variable as key,
     * and gives the label of the variable as value. This index also corresponds to a column
     * in the simplex table
     * </p>
     *
     * <p>
     * Note that the index of a variable is the column for that variable in the simplex table.
     * For example, if variable <tt>x</tt> is at column 2 of the simplex table, index of
     * variable <tt>x</tt> is 2
     * </p>
     */
    private final Map<Integer, String> indexVariableMap = new HashMap<>();

    /**
     * List of indices of the slack variables. Note that the index of a variable is the
     * column for that variable in the simplex table. For example, if variable <tt>x</tt>
     * is at column 2 of the simplex table, index of variable <tt>x</tt> is 2
     */
    private final List<Integer> slackVariableIndices = new ArrayList<>();

    /**
     * Adds a constraint of the form (c1*x1)+(c2*x2)+...+(cn*xn)<=b to the problem
     */
    @Override
    public void addConstraint(LPConstraint constraint) {

        // Add a slack variable to the constraint to convert it to an equation
        constraint.addSlack("S" + slackVarCount);

        // Add all the variables present in the constraint into variablesSet
        for (LPTerm term : constraint.getTerms()) {
            if (!variablesSet.contains(term.variableLabel)) {

                // Add indices of the slack variables in slackVariableIndices
                // This will be needed because the slack variables are the basic variables
                // initially
                if (term.isSlack) {
                    slackVariableIndices.add(varCount);
                }

                indexVariableMap.put(varCount, term.variableLabel);
                variablesSet.add(term.variableLabel);
                varCount++;
            }
        }

        constraints.add(constraint);

        // One slack variable per constraint
        slackVarCount++;
    }

    /**
     * @param objective The objective function to maximize
     * @return The maximum value of the objective function, and value of the
     * variables that yields that maximum, encapsulated in an
     * {@link LPResult LRResult}
     */
    @Override
    public LPResult maximize(LPObjective objective) {

        // Build a simplex table
        SimplexTable table = new SimplexTable(
                slackVarCount,
                getTotalVariableCount() - slackVarCount,
                slackVariableIndices,
                objective,
                indexVariableMap,
                constraints);

        // Update the table till the optimum point is reached
        while (!table.optimumPointReached()) {
            table.iterate();
        }

        return table.getOptimumResult();

    }

}