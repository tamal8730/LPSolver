package simplexlpsolver;
import java.util.List;
import java.util.Map;

import core.LPConstraint;
import core.LPObjective;
import core.LPResult;
import core.LPVariable;

/**
 * <p>
 * A data structure representing a Simplex Tableau used in Simplex Algorithm to
 * solve Linear Programming Problems
 * </p>
 * 
 * <p>
 * 
 * <pre>
 * Let us consider the problem,
 * 
 * Maximize z = 40x + 30y
 * 
 * Subject to,
 * x + y &lt;= 12
 * 2x + y &lt;= 16
 * x&gt;=0, y&gt;=0
 * 
 * The initial table for this problem would be:
 * 
 * coeff z          x   y   s0  s1   |   b
 * 0        s0      1   1   1   0    |   12      &lt;- x + y + s0 = 12
 * 0        s1      2   1   0   1    |   16      &lt;- 2x + y + s1 = 16
 * ------------------------------------------
 * cj-zj            40  30  0   0        0
 * 
 * </pre>
 * </p>
 */
public class SimplexTable {

    /**
     * Count of non basic variables
     */
    final int nonBasicVariableCount;

    /**
     * Count of basic variables
     */
    final int basicVariableCount;

    /**
     * List of indices of all the basic variables
     */
    final List<Integer> basicVariableIndices;

    /**
     * The main table, initially filled with coefficients of all the constraints.
     * This table is updated over many iterations till we get the maximum value of
     * objective function
     */
    final private double[][] table;

    /**
     * This map takes the index of a variable as key and gives the label of that
     * variable
     */
    private final Map<Integer, String> indexVariableMap;

    /**
     * Vector of constants <tt>bi</tt> found in the constraints
     */
    final private double[] constants;

    /**
     * Vector cj-zj
     */
    final private double[] cjMinusZj;

    /**
     * The stopping criteria in finding maximum using Simplex Algorithm is when the
     * vector cjMinusZj has all negative entries.
     *
     * <p>
     * In an iteration of the Simplex Algorithm, we take out a basic variable from
     * the table, and enter a non-basic variable in place of it. This entering
     * now becomes a basic variable and the leaving variable becomes a non-basic
     * variable.
     * </p>
     * 
     * <p>
     * The variable corresponding to the column with the maximum cj-zj value becomes
     * the entering variable. So to find the entering variable at every iteration,
     * we need to keep track of the maximum cj-zj value and its index
     *
     * </p>
     * Index of the the maximum value in vector cjMinusZj, used to determine the
     * entering variable
     */
    private int maxCjMinusZjIndex = 0;

    /**
     * The maximum value in vector cjMinusZj, used to determine the
     * entering variable
     */
    private double maxCjMinusZj = -1;

    /**
     * The objective function to be maximized
     */
    final private LPObjective objective;

    /**
     * <tt>true</tt> when the maximum value of the objective function is reached
     */
    private boolean optimumPointReached = false;

    /**
     * @return <tt>true</tt> when the maximum value of the objective function is
     *         reached
     */
    boolean optimumPointReached() {
        return optimumPointReached;
    }

    /**
     * 
     * @param basicVariableCount    Count of basic variables. It is the
     *                              same as the count of slack variables
     * @param nonBasicVariableCount Count of non-basic variables. Initially, all the
     *                              non-slack variables are non-basic
     * @param basicVariableIndices  List of indices of all the basic variables.
     *                              Initially, it is same as the indices of the
     *                              slack variables
     * @param objective             The objective function to be maximized
     * @param indexVariableMap      index to label map
     * @param constraints           List of constraints of the form
     *                              (a1*x1)+(a2*x2)+...+(an*xn)&lt=b
     */
    SimplexTable(int basicVariableCount, int nonBasicVariableCount,
            List<Integer> basicVariableIndices,
            LPObjective objective,
            Map<Integer, String> indexVariableMap,
            List<LPConstraint> constraints) {

        this.objective = objective;
        this.basicVariableCount = basicVariableCount;
        this.nonBasicVariableCount = nonBasicVariableCount;
        this.indexVariableMap = indexVariableMap;
        this.basicVariableIndices = basicVariableIndices;

        constants = new double[basicVariableCount];
        cjMinusZj = new double[nonBasicVariableCount + basicVariableCount + 1];
        table = new double[basicVariableCount][nonBasicVariableCount + basicVariableCount];

        fillInitialTable(objective, constraints);
    }

    /**
     * Construct the initial tableau with the specified objective function and
     * constraints
     */
    private void fillInitialTable(LPObjective objective, List<LPConstraint> constraints) {

        boolean allNegativesInCjMinusZj = true;

        for (int row = 0; row < basicVariableCount; row++) {
            LPConstraint constraint = constraints.get(row);
            for (int col = 0; col < nonBasicVariableCount + basicVariableCount; col++) {

                String colVariableLabel = indexVariableMap.get(col);
                table[row][col] = constraint.getCoefficientOf(colVariableLabel);

                if (row == 0) {

                    cjMinusZj[col] = objective.getCoefficientOf(colVariableLabel);

                    if (cjMinusZj[col] > 0) {
                        allNegativesInCjMinusZj = false;
                    }

                    // Remember the maximum cj-zj value and index
                    // To be used in determining the entering variable
                    if (cjMinusZj[col] > maxCjMinusZj) {
                        maxCjMinusZj = cjMinusZj[col];
                        maxCjMinusZjIndex = col;
                    }

                }
            }
            constants[row] = constraint.constant;
        }

        // If we have all negatives in cj-zj already, we are at the optimum point
        optimumPointReached = allNegativesInCjMinusZj;

    }

    /**
     * @return The maximum value of the objective function and value of the
     *         variables in the objective function that gives the maximum, all
     *         encapsulated in an {@link LPResult LPResult}
     * 
     * @apiNote Should be called only when {@code optimumPointReached=true}
     */
    LPResult getOptimumResult() {
        LPVariable[] vars = new LPVariable[objective.varCount()];
        int idx = 0;
        for (int i = 0; i < basicVariableCount; i++) {
            String varLabel = indexVariableMap.get(basicVariableIndices.get(i));
            if (objective.getCoefficientOf(varLabel) != 0) {
                vars[idx++] = new LPVariable(varLabel, constants[i]);
            }
        }
        return new LPResult(vars, getMaximumValue());
    }

    /**
     * @return The maximum value of the objective function
     * @apiNote Should be called only when {@code optimumPointReached=true}
     */
    double getMaximumValue() {
        return -cjMinusZj[cjMinusZj.length - 1];
    }

    /**
     * Run one iteration of the Simplex Algorithm
     * 
     * @apiNote Should be called repeatedly till {@code optimumPointReached=true}
     */
    void iterate() {

        // entering variable = var at index maxCjMinusZjIndex
        int leavingVariableRowIndex = findLeavingVariableRowIndex(maxCjMinusZjIndex);
        basicVariableIndices.set(leavingVariableRowIndex, maxCjMinusZjIndex);

        int pivotRow = leavingVariableRowIndex;
        int pivotColumn = maxCjMinusZjIndex;

        // Divide pivotRow by pivot element
        divideRow(pivotRow, table[pivotRow][pivotColumn]);

        // Update table
        optimumPointReached = updateTable(pivotRow, pivotColumn);

        // Turn basic variable submatrix into identity
        makeBasicIdentity();
        cjMinusZj[pivotColumn] = 0;

    }

    /**
     * @return <tt>true</tt> if cjMinusZj contains only negatives after the update
     */
    private boolean updateTable(int pivotRow, int pivotColumn) {

        double pivot = table[pivotRow][pivotColumn];

        for (int row = 0; row < basicVariableCount; row++) {
            if (row == pivotRow) {
                continue;
            }
            double entryOnColumnOfPivot = table[row][pivotColumn];
            for (int col = 0; col < basicVariableCount + nonBasicVariableCount; col++) {
                if (col == pivotColumn) {
                    continue;
                }
                double entryOnRowOfPivot = table[pivotRow][col];
                table[row][col] -= ((entryOnRowOfPivot * entryOnColumnOfPivot) / pivot);
            }
        }

        // update constants
        double entryOnRowOfPivot = constants[pivotRow];
        for (int row = 0; row < basicVariableCount; row++) {
            if (row == pivotRow) {
                continue;
            }
            constants[row] -= ((table[row][pivotColumn] * entryOnRowOfPivot) / pivot);
        }

        // update cj-zj
        maxCjMinusZj = 0;
        maxCjMinusZjIndex = 0;
        boolean allNegativesInCjMinusZj = true;
        for (int col = 0; col < basicVariableCount + nonBasicVariableCount; col++) {
            if (col == pivotColumn) {
                continue;
            }
            cjMinusZj[col] -= ((table[pivotRow][col] * cjMinusZj[pivotColumn]) / pivot);
            if (cjMinusZj[col] > 0) {
                allNegativesInCjMinusZj = false;
            }
            if (cjMinusZj[col] > maxCjMinusZj) {
                maxCjMinusZj = cjMinusZj[col];
                maxCjMinusZjIndex = col;
            }
        }

        cjMinusZj[cjMinusZj.length - 1] -= ((constants[pivotRow] * cjMinusZj[pivotColumn]) / pivot);

        return allNegativesInCjMinusZj;

    }

    /**
     * Updates the columns corresponding to the basic variables, such that the
     * submatrix formed by it becomes identity
     */
    private void makeBasicIdentity() {

        for (int i = 0; i < basicVariableCount; i++) {
            int colIndex = basicVariableIndices.get(i);
            for (int j = 0; j < basicVariableCount; j++) {
                if (i == j) {
                    table[j][colIndex] = 1;
                } else {
                    table[j][colIndex] = 0;
                }
            }
        }

    }

    /**
     * Divide an entire row of the table by divisor
     */
    private void divideRow(int rowIndex, double divisor) {
        for (int col = 0; col < basicVariableCount + nonBasicVariableCount; col++) {
            table[rowIndex][col] /= divisor;
        }
        constants[rowIndex] /= divisor;
    }

    /**
     * 
     * @return The row index of the leaving variable
     */
    private int findLeavingVariableRowIndex(int enteringVariableIndex) {

        double minRatio = Double.POSITIVE_INFINITY;
        int leavingVarRowIndex = 0;

        for (int i = 0; i < basicVariableCount; i++) {

            double valueAtEnteringColumnAndCurrBasicVarIndex = table[i][enteringVariableIndex];
            if (valueAtEnteringColumnAndCurrBasicVarIndex == 0) {
                continue;
            }
            double ratio = constants[i] / valueAtEnteringColumnAndCurrBasicVarIndex;

            if (ratio > 0 && ratio < minRatio) {
                minRatio = ratio;
                leavingVarRowIndex = i;
            }

        }

        return leavingVarRowIndex;

    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("\t");
        for (int i = 0; i < basicVariableCount + nonBasicVariableCount; i++) {
            String varLabel = indexVariableMap.get(i);
            s.append(varLabel).append("\t");
        }
        s.append("\n");
        for (int i = 0; i < basicVariableCount; i++) {

            int bvIndex = basicVariableIndices.get(i);
            String bvLabel = indexVariableMap.get(bvIndex);

            s.append(bvLabel).append("\t");
            for (int j = 0; j < nonBasicVariableCount + basicVariableCount; j++) {
                s.append(table[i][j]).append("\t");
            }
            s.append(constants[i]).append("\t");
            s.append('\n');
        }
        s.append('\t');
        for (double c : cjMinusZj) {
            s.append(c).append("\t");
        }
        s.append("\n");
        return s.toString();
    }

}
