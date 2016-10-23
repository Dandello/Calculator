import java.math.BigDecimal;
import java.util.IllegalFormatException;

import static org.junit.Assert.*;

public class ExprSolverTest {

    @org.junit.Test
    public void operationResultTest() throws Exception {
        String result;

        assertEquals("summary", "4" , new ExprSolver().calculate("2+2").getResult());

        assertEquals("substract", "23.729" , new ExprSolver().calculate("27-3.271").getResult());

        assertEquals("multiply", "144" , new ExprSolver().calculate("12*12").getResult());

        assertEquals("degree", "169" , new ExprSolver().calculate("13^2").getResult());

        assertEquals("divide", "4" , new ExprSolver().calculate("(98.1+1.9)/25").getResult());

        assertEquals("factorial", "6227020823" , new ExprSolver().calculate("13!+23").getResult());

        result = new ExprSolver().calculate("32!").getResult();
        assertTrue("BigInteger factorial", !result.equals("error") && new BigDecimal(result).compareTo(BigDecimal.ZERO) > 0);

        result = new ExprSolver().calculate("50000!").getResult();
        assertTrue("BigInteger factorial #2", !result.equals("error") && new BigDecimal(result).compareTo(BigDecimal.ZERO) > 0);

        assertEquals("right operation order", "14551915228366851806640635" , new ExprSolver().calculate("(2+3)^3!^2+10").getResult());

    }
    @org.junit.Test(expected = IllegalStateException.class)
    public void FractionalFactorialTest() throws Exception {
        new ExprSolver().calculate("12.2!");
    }
    @org.junit.Test(expected = IllegalStateException.class)
    public void DivisionByZeroTest() throws Exception {
        new ExprSolver().calculate("1/0");
    }
    @org.junit.Test(expected = IllegalStateException.class)
    public void NegativeFactorialTest() throws Exception {
        new ExprSolver().calculate("(-12)!");
    }
    @org.junit.Test(expected = IllegalStateException.class)
    public void MissedLeftBracketTest() throws Exception {
        new ExprSolver().calculate("120-33)*2");
    }
    @org.junit.Test(expected = IllegalStateException.class)
    public void MissedRightBracketTest() throws Exception {
        new ExprSolver().calculate("((1+2)*(3+4)");
    }
    @org.junit.Test(expected = IllegalStateException.class)
    public void IllegalSymbolTest() throws Exception {
        new ExprSolver().calculate("12x+33y");
    }
    @org.junit.Test(expected = IllegalStateException.class)
    public void MissedOperandTest() throws Exception {
        new ExprSolver().calculate("-12+12*");
    }
    @org.junit.Test(expected = IllegalStateException.class)
    public void FractionalDegreeTest() throws Exception {
        new ExprSolver().calculate("3^1.24");
    }
}