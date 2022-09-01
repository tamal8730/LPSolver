package core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * An object that represents inequations of the form
 * (c1*x1)+(c2*x2)+(c3*x3)+...+(cn*xn) &lt;=b, used as constraints of a Linear
 * Programming Problem
 * 
 * @author Tamal Das
 */
public class LPConstraint {

    /**
     * This map takes in the label of a variable as key, and gives the coefficient
     * of that variable in the inequation as value
     */
    final private Map<String, Double> variableCoeffMap = new HashMap<>();

    /**
     * Array of terms present in the inequation
     */
    final private LPTerm[] terms;

    /**
     * The constant term <tt>b</tt> in the inequation
     */
    final public double constant;

    /**
     * Slack variable to convert the inequation into an equation
     */
    private LPTerm slack;

    /**
     * 
     * @param terms    Array of terms present in the inequation
     * @param constant The constant term <tt>b</tt> in the inequation
     */
    public LPConstraint(LPTerm[] terms, double constant) {
        this.constant = constant;
        this.terms = terms;
        for (LPTerm term : terms) {
            variableCoeffMap.put(term.variableLabel, term.coefficient);
        }
    }

    /**
     * Adds a slack variable, needed to convert the inequation into an equation
     * 
     * @param label Label for the slack variable
     */
    public void addSlack(String label) {
        slack = LPTerm.slack(label);
        variableCoeffMap.put(label, 1d);
    }

    /**
     * Returns the coefficient of the variable labelled <tt>variableLabel</tt> in
     * the inequation. If the inequation does not contain <tt>variableLabel</tt>, it
     * returns 0
     */
    public double getCoefficientOf(String variableLabel) {
        Double coeff = variableCoeffMap.get(variableLabel);
        return coeff == null ? 0 : coeff;
    }

    /**
     * Iterable to iterate through all the terms in the inequation
     */
    public Iterable<LPTerm> getTerms() {
        return () -> new Iterator<>() {

            private int idx = 0;

            @Override
            public boolean hasNext() {
                return slack == null ? idx < terms.length : idx <= terms.length;
            }

            @Override
            public LPTerm next() {
                LPTerm term = slack != null && idx == terms.length ? slack : terms[idx];
                idx++;
                return term;
            }

        };
    }

}