import pipe.Pipe;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Pipe pipe = new Pipe();
        pipe.createFilters();
        pipe.executeFilters();
    }
}