import java.util.ArrayList;

/**
 * Created by Артем on 25.09.2016.
 */
public class ResultData {
    private String result;
    private ArrayList<String> log = new ArrayList<>();
    public String getResult() {
        return result.toString();
    }
    public void addLog (String operation) {
        log.add(operation);
    }
    public void setResult (String result) {
        this.result = result;
    }
    public ArrayList<String> getLog() {
        return log;
    }
}
