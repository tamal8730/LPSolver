package core;

/**
 * A term is a pair of a variable and its coefficient in a linear expression.
 * For example, in the linear inequation 2x+3y-6z<=8, (2,x),(3,y) and (-6,z) are
 * the terms. The constant is ignored.
 */
public class LPTerm {

    /**
     * Label of the variable in the term
     */
    final public String variableLabel;

    /**
     * Coefficient of the variable in the term
     */
    final public double coefficient;

    /**
     * <tt>true</tt> if the variable in the term is a slack variable. Slack
     * variables are added in the constraints of an LP problem to convert the
     * inequations to an equation.
     */
    final public boolean isSlack;

    /**
     * Creates a non-slack core.LPTerm with unit coefficient
     */
    public LPTerm(String variableLabel) {
        this.variableLabel = variableLabel;
        coefficient = 1;
        isSlack = false;
    }

    /**
     * Creates a non-slack core.LPTerm
     */
    public LPTerm(double coefficient, String variableLabel) {
        this.variableLabel = variableLabel;
        this.coefficient = coefficient;
        isSlack = false;
    }

    /**
     * Creates an core.LPTerm
     */
    private LPTerm(double coefficient, String variableLabel, boolean isSlack) {
        this.coefficient = coefficient;
        this.variableLabel = variableLabel;
        this.isSlack = isSlack;
    }

    /**
     * Creates a slack core.LPTerm with unit coefficient
     */
    static LPTerm slack(String variableLabel) {
        return new LPTerm(1, variableLabel, true);
    }

}