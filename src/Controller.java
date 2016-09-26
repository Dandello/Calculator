import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Time;
import java.util.concurrent.*;

/**
 * Created by Артем on 25.09.2016.
 */
public class Controller {
    private ExprSolver exprSolver;
    private View view;
    private ResultData resultData;

    public Controller(View view) {
        this.view = view;
        view.setActionListener(new ComputationListener());
    }

    class ComputationListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String expression = view.getInputDisplayText();
            /*exprSolver.clear();
            try {
                resultData = exprSolver.calculate(expression);
                view.resultDisplay(resultData);
            } catch(IllegalStateException exception) {
                view.displayError(exception.getMessage());

            }*/
            ExecutorService exec = Executors.newCachedThreadPool();
            Future<ResultData> result = exec.submit(new EvaluationThread(expression));
            try {
                resultData = result.get(5000, TimeUnit.MILLISECONDS);
                view.resultDisplay(resultData);
            } catch(InterruptedException ie) {
                view.displayError("Something went wrong. Please, try again.");
            } catch(TimeoutException te) {
                view.displayError("Timeout on response! Try again, please!");
            }  catch (ExecutionException ee) {
                view.displayError("Something went wrong. Please, try again.");
            } finally {
                exec.shutdown();
            }

        }
    }

    class EvaluationThread implements Callable<ResultData> {
        private String expression;
        private ResultData resultData = new ResultData();
        public EvaluationThread(String expression) {
            this.expression = expression;
        }
        public ResultData call() {
            try {
                resultData = new ExprSolver().calculate(expression);
            } catch(IllegalStateException e) {
                resultData.setResult("Error");
                resultData.addLog(e.getMessage());
            } finally {
                return resultData;
            }
        }

    }
}