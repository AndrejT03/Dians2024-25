package timer;

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
        long minutes = (long) (durationInSeconds / 60);
        double seconds = durationInSeconds % 60;
        return String.format("Total execution time: %d minutes %.3f seconds\n", minutes, seconds);
    }
}
