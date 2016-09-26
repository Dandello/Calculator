import java.util.*;

import java.lang.Math;

/**
 * Created by Артем on 14.09.2016.
 */
public class ExprSolver  {
    private static final HashMap<String,Integer> OPERATIONS_PRIORITY;
    private int nextPosition = 0;
    private int currentPosition = 0;
    private ResultData resultData = new ResultData();
    private static final Set<String> OPERATIONS;
    static {
        OPERATIONS_PRIORITY = new HashMap<String,Integer>();
        OPERATIONS_PRIORITY.put("*",2);
        OPERATIONS_PRIORITY.put("^",1);
        OPERATIONS_PRIORITY.put("!",0);
        OPERATIONS_PRIORITY.put("/",2);
        OPERATIONS_PRIORITY.put("+",3);
        OPERATIONS_PRIORITY.put("-",3);
        OPERATIONS = new HashSet<String>(OPERATIONS_PRIORITY.keySet());
        OPERATIONS.add("(");
        OPERATIONS.add(")");
    }
    public ArrayList<String> postfixForm (String expression) { // реализация алгоритма сортировочной станции
        Stack<String> stack = new Stack<String>();
        ArrayList<String> out = new ArrayList<String>();
        // подготовительная часть - убираем пробелы, превращаем унарный минус в бинарный
        expression = expression.replace(" ", "").replace(",", ".").replace("(-", "(0-");
        if (expression.charAt(0) == '-')
            expression = "0" + expression;
        while(currentPosition!=expression.length()) {
            String nextOperation = getNextOperation(expression, currentPosition);
            if(nextPosition==expression.length())
                break;
            if(nextPosition > currentPosition)
                out.add(expression.substring(currentPosition, nextPosition));
            if(isLeftBracket(nextOperation))
                stack.push(nextOperation);
            else if(isRightBracket(nextOperation)) {
                if(stack.empty())
                    throw new IllegalStateException("Expression is not correct. Check it and try again.");
                while(!isLeftBracket(stack.peek())) {
                    out.add(stack.pop());
                    if(stack.empty())
                        throw new IllegalStateException("Left bracket is missed! Check the expression and try again.");
                }
                stack.pop();
            }
            else {
                while(!stack.empty() && !isLeftBracket(stack.peek()) && (
                        isRightAssoc(nextOperation) && OPERATIONS_PRIORITY.get(nextOperation) > OPERATIONS_PRIORITY.get(stack.peek()) ||
                                !isRightAssoc(nextOperation) && OPERATIONS_PRIORITY.get(nextOperation) >= OPERATIONS_PRIORITY.get(stack.peek()) ))
                    out.add(stack.pop());
                stack.push(nextOperation);
            }
            currentPosition = nextPosition + nextOperation.length();
        }
        if(currentPosition!=expression.length())
            out.add(expression.substring(currentPosition));
        while(!stack.empty()) {
            if(isLeftBracket(stack.peek()))
                throw new IllegalStateException("There ");
            out.add(stack.pop());
        }
        return out;
    }

    public ResultData calculate(String expression) {
        ArrayList<String> rpn = postfixForm(expression);
        Stack<Double> stack = new Stack<Double>();
        for(String token: rpn) {
            if(!OPERATIONS.contains(token)) {
                try {
                    stack.push(Double.parseDouble(token));
                } catch(NumberFormatException nfe) {
                    throw new IllegalStateException("Invalid format of the operand! Check an expression!");
                }
            }
            else
                stack.push(operationResult(token, stack));
        }
        if(stack.size()!=1)
            throw new IllegalStateException("Expression is not correct! Check it and try again.");
        resultData.setResult(correctForm(stack.peek()));
        return resultData;
      //  notifyObservers(resultData);
      //  return stack.pop();
    }

    private String getNextOperation(String expression, int idx) {
        int nextOperIdx = expression.length();
        int len = 0;
        for (String operation : OPERATIONS) {
            int i = expression.indexOf(operation, idx);
            if (i >= 0 && nextOperIdx > i) {
                nextOperIdx = i;
                len = operation.length();
            }
        }
        nextPosition = nextOperIdx;
        return expression.substring(nextOperIdx, nextOperIdx + len);
    }

    private boolean isLeftBracket (String operation){
        return operation.equals("(");
    }
    private boolean isRightBracket (String operation){
        return operation.equals(")");
    }
    private boolean isRightAssoc(String operation) {
        return operation.equals("^") || operation.equals("!");
    }
    private boolean isUnary(String operation) {
        return operation.equals("!") ;
    }

    private Double operationResult (String operation, Stack<Double> stack) {
        Double operand2 = 0.0, operand1 = 0.0;
        try {
            operand1 = stack.pop();
            if (!isUnary(operation))
                operand2 = stack.pop();
        } catch(EmptyStackException exception) {
            throw new IllegalStateException("The operand is missing! Check the expression and try again!");
        }
        Double result = 0.0;
        switch (operation) {
            case ("+"):
                result = operand1 + operand2;
                resultData.addLog(correctForm(operand2) + " + " + correctForm(operand1) + " = " + correctForm(result));
                break;
            case ("-"):
                result = operand2 - operand1;
                resultData.addLog(correctForm(operand2) + " - " + correctForm(operand1) + " = " + correctForm(result));
                break;
            case ("*"):
                result = operand2 * operand1;
                resultData.addLog(correctForm(operand1) + " * " + correctForm(operand2) + " = " + correctForm(result));
                break;
            case ("/"):
                result = operand2 / operand1;
                resultData.addLog(correctForm(operand2) + " / " + correctForm(operand1) + " = " + correctForm(result));
                break;
            case ("!"):
                result = new Double(factorial(operand1.longValue()));
                resultData.addLog( correctForm(operand1) + "!" + " = " + correctForm(result));
                break;
            case ("^"):
                result = Math.pow(operand2, operand1);
                resultData.addLog(correctForm(operand2) + " ^ " + correctForm(operand1) + " = " + correctForm(result));
                break;
        }
        if(Thread.currentThread().isInterrupted())
            throw new IllegalStateException();
        return result;
    }
    private long factorial(long num) {
        if(num == 0)
            return (long)1;
        else
            return num * factorial(num-1);
    }

    public void clear() {
        resultData = new ResultData();
        nextPosition = 0;
        currentPosition = 0;
    }
    public String correctForm (double value) {
        long iPart = (long) value;
        double fPart = value - iPart;
        if (fPart == 0.0)
            return new Long(iPart).toString();
        else
            return new Double(value).toString();
    }
}
