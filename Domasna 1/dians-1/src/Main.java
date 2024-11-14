import pipe.Pipe;
import timer.Timer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Timer timer = new Timer();
        timer.startTimer();

        Pipe pipe = new Pipe();
        pipe.createFilters();
        pipe.executeFilters();

        timer.endTimer();
        System.out.printf(timer.getTime());
    }
}