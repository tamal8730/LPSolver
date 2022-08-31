package core;

import java.util.HashMap;
import java.util.Map;

/**
 * An object that represents the objective function of the form
 * (a1*x1)+(a2*x2)+(a3*x3)+...+(an*xn) to be optimised in a Linear Programming
 * Problem
 * 
 * @author Tamal Das
 */
public class LPObjective {

    /**
     * This map takes in the label of a variable as key, and gives the coefficient
     * of that variable in the objective function as value
     */
    final private Map<String, Double> variableCoeffMap = new HashMap<>();

    /**
     * Array of terms present in the objective function
     */
    final private LPTerm[] terms;

    /**
     * Returns the count of terms in the objective function
     */
    public int varCount() {
        return terms.length;
    }

    /**
     * @param terms Array of terms present in the objective function
     */
    public LPObjective(LPTerm[] terms) {
        this.terms = terms;
        for (LPTerm term : terms) {
            variableCoeffMap.put(term.variableLabel, term.coefficient);
        }
    }

    /**
     * Returns the coefficient of the variable labelled <tt>variableLabel</tt> in
     * the objective function. If the function does not contain
     * <tt>variableLabel</tt>, it returns 0
     */
    public double getCoefficientOf(String variableLabel) {
        Double coeff = variableCoeffMap.get(variableLabel);
        return coeff == null ? 0 : coeff;
    }

}
