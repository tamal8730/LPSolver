package core;

/**
 * An object that represents the result/solution of a Linear Programming
 * Problem. It encapsulates the optimum value of the objective function, and
 * value of the variables that yields the optimum result
 *
 * @author Tamal Das
 */
public record LPResult(LPVariable[] variables, double optimumObjectiveValue) {

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");

        builder.append("\toptimum_variable_values: {\n");
        for (LPVariable var : variables) {
            builder.append("\t\t");
            builder.append(var.toString());
            builder.append('\n');
        }
        builder.append("\t}\n");

        builder.append("\toptimum_obj_func_value: ");
        builder.append(optimumObjectiveValue);
        builder.append('\n');

        builder.append("}\n");
        return builder.toString();
    }

}
