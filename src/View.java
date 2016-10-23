import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;

/**
 * Created by Артем on 25.09.2016.
 */
public class View extends JFrame  {
    final static Color  HILIT_COLOR = Color.lightGray;
    final static Color  ERROR_COLOR = Color.PINK;
    private JTextField inputDisplay;
    private JScrollPane scrollPane;
    private JLabel label;
    private JPanel panel;

    private JTextArea logDisplay;
    private ProgressFrame progressFrame;
    public View() {
        super("Calculator");
        setWindowPreferences();
        inputDisplay = new JTextField();
        configureInputDisplay();
        logDisplay = new JTextArea(15,35);
        configureLogDisplay();
        panel = new JPanel();
        scrollPane = new JScrollPane(logDisplay);
        label = new JLabel("Type the arithmetic expression and press ENTER");
        configureLabel();
        panel.add(inputDisplay);
        panel.add(label);
        panel.add(scrollPane);
        progressFrame = new ProgressFrame(this);
//        panel.add(progressFrame);
        this.getContentPane().add(panel);
        inputDisplay.requestFocus();
        this.setVisible(true);

    }

    private void setWindowPreferences() {
        setVisible(true);
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(520, 320);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2 - getSize().width/2, dim.height/2 - getSize().height/2);
    }
    private void configureInputDisplay() {
        inputDisplay.setColumns(25);
        inputDisplay.setEditable(true);
        inputDisplay.setHorizontalAlignment(JTextField.LEFT);
        inputDisplay.setFont(new Font("SansSerif", Font.PLAIN, 20));
    }
    public void showProgress() {
        progressFrame.setVisible(true);
    }
    public void closeProgress() {
        progressFrame.setVisible(false);
    }
    private void configureLogDisplay() {
        logDisplay.setEditable(false);
        logDisplay.setBackground(HILIT_COLOR);
        logDisplay.setFont(new Font("Times New Roman", Font.PLAIN, 16));
    }
    private void configureLabel() {
        label.setFont(new Font("Times New Roman", Font.ITALIC, 10));
    }

    public String getInputDisplayText(){
        return inputDisplay.getText();
    }

    public void displayError(String errorMessage) {
        JOptionPane.showMessageDialog(panel, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
    public void resultDisplay(ResultData resultData) {
        if(resultData.getResult().equals("Error")) {
            displayError(resultData.getLog().get(0));
            return;
        }
        logDisplay.setText("Value of the expression:\n");
        logDisplay.append("\t" + resultData.getResult()+"\n");
        logDisplay.append("Sequence of operations:\n");

        int idx = 1;
        for(String operation : resultData.getLog()) {
            logDisplay.append( "     " + idx + ") " + operation + "\n");
            idx++;
        }
    }
    public void setActionListener(ActionListener actionListener) {
            inputDisplay.addActionListener(actionListener);
    }
    public void setButtonListener(ActionListener actionListener) {
        progressFrame.cancelButton.addActionListener(actionListener);
    }


    class ProgressFrame extends JDialog {
        private JProgressBar progressBar;
        private JFrame motherFrame;
        private JLabel label;
        private JButton cancelButton;

        public ProgressFrame(JFrame frame) {
            super(frame, "Wait...", ModalityType.APPLICATION_MODAL);
            progressBar = new JProgressBar();
            cancelButton = new JButton("Cancel");
            label = new JLabel("The expression is evaluating...");
            setWindowPreferences();
            motherFrame = frame;

        }
        private void setWindowPreferences() {
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            progressBar.setIndeterminate(true);
            add(label, BorderLayout.NORTH);
            add(cancelButton, BorderLayout.SOUTH);
            add(progressBar, BorderLayout.CENTER);
            setSize(250, 75);
            setAlwaysOnTop(true);
            setLocationRelativeTo(motherFrame);
            setUndecorated(true);
        }
    }
}
