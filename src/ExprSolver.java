import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
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
    private static final Set<String> FUNCTIONS;
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
        FUNCTIONS = new HashSet<>();
        FUNCTIONS.add("sin");
        FUNCTIONS.add("cos");
        FUNCTIONS.add("tg");
        FUNCTIONS.add("ctg");

    }
    public ArrayList<String> postfixForm (String expression) {
        Stack<String> stack = new Stack<String>();
        ArrayList<String> out = new ArrayList<String>();
        expression = expression.replace(" ", "").replace(",", ".").replace("(-", "(0-");
        if (expression.charAt(0) == '-')
            expression = "0" + expression;
        expression = expression.toLowerCase();
        while(currentPosition!=expression.length()) {
            String nextOperation = getNextOperation(expression, currentPosition);
            if(nextPosition==expression.length())
                break;
            if(nextPosition > currentPosition) {
                String token = expression.substring(currentPosition, nextPosition);
                if (FUNCTIONS.contains(token))
                    stack.push(token);
                else
                    out.add(token);
            }
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
                if(!stack.empty() && FUNCTIONS.contains(stack.peek()))
                    out.add(stack.pop());

            }
            else {
                while(!stack.empty() && !isLeftBracket(stack.peek()) && !FUNCTIONS.contains(stack.peek()) &&
                        (isRightAssoc(nextOperation) && OPERATIONS_PRIORITY.get(nextOperation) > OPERATIONS_PRIORITY.get(stack.peek()) ||
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
                throw new IllegalStateException("There is a problem....");
            out.add(stack.pop());
        }
        return out;
    }

    public ResultData calculate(String expression) {
        ArrayList<String> rpn = postfixForm(expression);
        Stack<BigDecimal> stack = new Stack<BigDecimal>();
        for(String token: rpn) {
            if(!OPERATIONS.contains(token)&&!FUNCTIONS.contains(token)) {
                try {
                    stack.push(new BigDecimal(token));
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
        return operation.equals("!")||FUNCTIONS.contains(operation) ;
    }

    private BigDecimal operationResult (String operation, Stack<BigDecimal> stack) {
        BigDecimal operand2 = new BigDecimal("0");
        BigDecimal operand1 = new BigDecimal("0");
        try {
            operand1 = stack.pop();
            if (!isUnary(operation))
                operand2 = stack.pop();
        } catch(EmptyStackException exception) {
            throw new IllegalStateException("The operand is missing! Check the expression and try again!");
        }
        BigDecimal result = new BigDecimal("0");
        switch (operation) {
            case ("+"):
                result = operand1.add(operand2);
                resultData.addLog(correctForm(operand2) + " + " + correctForm(operand1) + " = " + correctForm(result));
                break;
            case ("-"):
                result = operand2.subtract(operand1);
                resultData.addLog(correctForm(operand2) + " - " + correctForm(operand1) + " = " + correctForm(result));
                break;
            case ("*"):
                result = operand1.multiply(operand2);
                resultData.addLog(correctForm(operand1) + " * " + correctForm(operand2) + " = " + correctForm(result));
                break;
            case ("/"):
                if(operand1.equals(BigDecimal.ZERO))
                    throw new IllegalStateException("Division by zero is not allowed! Check your expression and try again.");
                result = operand2.divide(operand1,new MathContext(8, RoundingMode.HALF_DOWN));
                resultData.addLog(correctForm(operand2) + " / " + correctForm(operand1) + " = " + correctForm(result));
                break;
            case ("!"):
                if(operand1.compareTo(BigDecimal.ZERO) < 0)
                    throw new IllegalStateException("Factorial of negative numbers is not possible! Check your expression and try again.");
                if(!isInteger(operand1))
                    throw new IllegalStateException("Factorial is possible on whole numbers only! Check your expression and try again.");
                result = new BigDecimal(factorial(operand1.intValue()));
                resultData.addLog( correctForm(operand1) + "!" + " = " + correctForm(result));
                break;
            case ("sin"):
                result = new BigDecimal(Math.sin(operand1.doubleValue()), new MathContext(8, RoundingMode.HALF_DOWN));
                resultData.addLog("sin(" + correctForm(operand1) + ") = " + correctForm(result));
                break;
            case ("cos"):
                result = new BigDecimal(Math.cos(operand1.doubleValue()), new MathContext(8, RoundingMode.HALF_DOWN));
                resultData.addLog("cos(" + correctForm(operand1) + ") = " + correctForm(result));
                break;
            case ("tg"):
                result = new BigDecimal(Math.tan(operand1.doubleValue()), new MathContext(8, RoundingMode.HALF_DOWN));
                resultData.addLog("tg(" + correctForm(operand1) + ") = " + correctForm(result));
                break;
            case ("ctg"):
                if(operand1.compareTo(BigDecimal.ZERO) == 0)
                    throw new IllegalStateException("Contangent of zero is illegible! Check your expression and try again.");
                result = new BigDecimal(1.0 / Math.tan(operand1.doubleValue()), new MathContext(8, RoundingMode.HALF_DOWN));
                resultData.addLog("ctg(" + correctForm(operand1) + ") = " + correctForm(result));
                break;
            case ("^"):
                if(!isInteger(operand1))
                    throw new IllegalStateException("Degree cannot be fractional! Check your expression and try again.");
                result = operand2.pow(operand1.intValue());
                resultData.addLog(correctForm(operand2) + " ^ " + correctForm(operand1) + " = " + correctForm(result));
                break;
        }
        if(Thread.currentThread().isInterrupted())
            throw new IllegalStateException();
        return result;
    }
    private  BigInteger factorial(int num) {
        BigInteger fact = BigInteger.valueOf(1);
        for (int i = 1; i <= num; i++) {
            fact = fact.multiply(BigInteger.valueOf(i));
            if(Thread.currentThread().isInterrupted())
                throw new IllegalStateException("Interrupt");

        }
        return fact;
    }

    private boolean isInteger(BigDecimal num) {
        return num.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;
    }

    private String correctForm(BigDecimal number) {
        if(number.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0)
            return number.toBigInteger().toString();
        else
            return number.toString();
    }
}
