import java.util.Scanner;
import java.util.HashMap;

public class Interpreter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //read the program
        System.out.println("Enter the program:");
        String program = scanner.nextLine();

        //split the program into assignments
        String[] assignments = program.split(";");

        //create a hashmap to store variables and their values
        HashMap<String, Integer> variables = new HashMap<>();

        //loop through each assignment
        for (String assignment : assignments) {

            //split the assignment into the identifier and expression
            String[] parts = assignment.split("=");

            //check if the assignment is valid
            if (parts.length != 2) {
                System.out.println("Syntax Error: Invalid assignment.");
                return;
            }

            //get the identifier
            String identifier = parts[0].trim();

            //check if the identifier is valid
            if (!isValidIdentifier(identifier)) {
                System.out.println("Syntax Error: Invalid identifier.");
                return;
            }

            //evaluate the expression and handle any errors
            int value = evaluateExpression(parts[1].trim(), variables);
            if (value == Integer.MIN_VALUE) {
                //an error was detected in the expression
                return;
            }

            //assign the value to the variable
            variables.put(identifier, value);
        }

        //print out the values of all variables
        System.out.println("Values of variables:");
        for (String identifier : variables.keySet()) {
            System.out.println(identifier + " = " + variables.get(identifier));
        }
    }
    private static boolean isValidIdentifier(String identifier) {

        //identifier must start with a letter or underscore
        //and can only contain letters, digits, or underscore
        return identifier.matches("[A-Za-z_]\\w*");
    }
    private static int evaluateExpression(String expression, HashMap<String, Integer> variables) {

        //split the expression into terms
        String[] terms = expression.split("[\\+\\-]");

        //initialize the total
        int total = 0;

        //loop through each term
        for (int i = 0; i < terms.length; i++) {

            //remove all whitespaces
            terms[i] = terms[i].replaceAll("\\s+", "");

            //check if the term is a valid fact
            int value = evaluateTerm(terms[i], variables);
            if (value == Integer.MIN_VALUE) {
                //an error was detected in the term
                return Integer.MIN_VALUE;
            }

            //if it is the first term or there is a plus sign before it, add the value to the total
            if (i == 0 || expression.charAt(expression.indexOf(terms[i]) - 1) == '+') {
                total += value;
            }

            //if there is a minus sign before it, subtract the value from the total
            else if (expression.charAt(expression.indexOf(terms[i]) - 1) == '-') {
                total -= value;
            }
        }

        //return the total
        return total;
    }
    private static int evaluateTerm(String term, HashMap<String, Integer> variables) {

        //split the term into factors
        String[] factors = term.split("\\*");

        //initialize the total
        int total = 1;

        //loop through each factor
        for (String factor : factors) {

            //check if the factor is a valid fact
            int value = evaluateFact(factor, variables);
            if (value == Integer.MIN_VALUE) {
                //an error was detected in the factor
                return Integer.MIN_VALUE;
            }

            //multiply the factor to the total
            total *= value;
        }

        //return the total
        return total;
    }
    private static int evaluateFact(String factor, HashMap<String, Integer> variables) {

        //remove all whitespaces
        factor = factor.replaceAll("\\s+", "");

        //check if the factor is a literal
        if (isLiteral(factor)) {
            return Integer.parseInt(factor);
        }

        //check if the factor is an identifier
        if (isValidIdentifier(factor)) {

            //check if the variable has been assigned a value
            if (!variables.containsKey(factor)) {
                System.out.println("Uninitialized variable error: " + factor);
                return Integer.MIN_VALUE;
            }

            //return the value of the variable
            return variables.get(factor);
        }

        //check if the factor is a nested expression
        if (factor.charAt(0) == '(' && factor.charAt(factor.length() - 1) == ')') {

            //remove the parenthesis
            String expression = factor.substring(1, factor.length() - 1);

            //evaluate the nested expression
            return evaluateExpression(expression, variables);
        }

        //invalid factor
        System.out.println("Syntax Error: Invalid factor.");
        return Integer.MIN_VALUE;
    }
    private static boolean isLiteral(String string) {

        //literal must start with a non-zero digit or zero
        //and can only contain digits
        return string.matches("[1-9]\\d*|0");
    }
}