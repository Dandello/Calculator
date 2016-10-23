import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Time;
import java.util.IllegalFormatCodePointException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.*;

public class Controller {
    private View view;
    private ExecutorService exec;
    Future<ResultData> result;

    public Controller(View view) {
        this.view = view;
        exec = Executors.newCachedThreadPool();
        view.setActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String expression = view.getInputDisplayText();
                result = exec.submit(new EvaluationThread(expression));
                view.showProgress();
            }
        });
        view.setButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result.cancel(true);
                view.closeProgress();
            }
        });
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
                SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                view.closeProgress();
                                view.resultDisplay(resultData);
                            }
                        });
                return resultData;
            } catch(IllegalStateException e) {
                resultData.setResult("Error");
                resultData.addLog(e.getMessage());
                SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                view.closeProgress();
                                if(!e.getMessage().equals("Interrupt"))
                                    view.resultDisplay(resultData);
                            }
                        });
                return resultData;
            }
        }

    }
}