package timer;

import java.text.DecimalFormat;

public class Timer {
    long startTime;
    long endTime;
    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void endTimer() {
        this.endTime = System.nanoTime();
    }

    public String getTime() {
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0;
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        return decimalFormat.format(durationInSeconds);
    }
}
