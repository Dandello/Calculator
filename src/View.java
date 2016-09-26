import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import java.awt.Color;

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
      //  panel.add(logDisplay);
        panel.add(scrollPane);
        this.getContentPane().add(panel);
        inputDisplay.requestFocus();
        this.setVisible(true);
    }

    private void setWindowPreferences() {
        setVisible(true);
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(520, 320);
    }
    private void configureInputDisplay() {
        inputDisplay.setColumns(25);
        inputDisplay.setEditable(true);
        inputDisplay.setHorizontalAlignment(JTextField.LEFT);
        Font newFont = new Font("SansSerif", Font.PLAIN, 20);
        inputDisplay.setFont(newFont);
    }
    private void configureLogDisplay() {
        logDisplay.setEditable(false);
        Font newFont = new Font("Times New Roman", Font.PLAIN, 16);
        logDisplay.setBackground(HILIT_COLOR);
        logDisplay.setFont(newFont);
    }
    private void configureLabel() {
        Font newFont = new Font("Times New Roman", Font.ITALIC, 10);
        label.setFont(newFont);
    }

    public String getInputDisplayText(){
        return inputDisplay.getText();
    }

    public void displayError(String errorMessage) {
        JOptionPane.showMessageDialog(panel, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
       // logDisplay.setText(errorMessage);
    }

    public void resultDisplay(ResultData resultData) {
        if(resultData.getResult().equals("Error")) {
            displayError(resultData.getLog().get(0));
            return;
        }
        logDisplay.setText("Value of the expression:\n");

        logDisplay.append("\t\t" + resultData.getResult()+"\n");
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
}
