package filters.impl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

public class InformationFilter extends WriteFilter {

    @Override
    public Map<String, String> execute(Map<String, String> codeDateMap) throws IOException {
        String todaysFormattedDate = getTodaysFormattedDate();

        for (Map.Entry<String, String> entry : codeDateMap.entrySet()) {
            String code = entry.getKey();
            String date = entry.getValue();

            if (!date.equals(todaysFormattedDate)) {
                String fileName = code + ".csv";
                try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                    FileWriter fileWriter = new FileWriter(fileName, true);
                    PrintWriter printWriter = new PrintWriter(fileWriter);

                    String[] writtenDateParts = date.split("\\.");
                    String[] todaysDateParts = todaysFormattedDate.split("\\.");
                    int writtenDay = Integer.parseInt(writtenDateParts[0]);
                    int writtenMonth = Integer.parseInt(writtenDateParts[1]);
                    int writtenYear = Integer.parseInt(writtenDateParts[2]);
                    int todaysDay = Integer.parseInt(todaysDateParts[0]);
                    int todaysMonth = Integer.parseInt(todaysDateParts[1]);
                    int todaysYear = Integer.parseInt(todaysDateParts[2]);

                    List<String> lines = Files.readAllLines(Paths.get(fileName));
                    lines.removeLast();
                    Files.write(Paths.get(fileName), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

                    if (todaysYear - writtenYear > 1) {
                        String queryString = getQueryString(false, writtenDay, 1, writtenMonth, 1, writtenYear, writtenYear + 1, code);
                        writeRows(code, queryString, printWriter);

                        for (int i = writtenYear + 1; i < todaysYear; i++) {
                            queryString = getQueryString(false, 1, 1, 1, 1, i, i + 1, code);
                            writeRows(code, queryString, printWriter);
                        }

                        queryString = getQueryString(true, 1, todaysDay, 1, todaysMonth, todaysYear, todaysYear, code);
                        writeRows(code, queryString, printWriter);

                    } else if (todaysYear - writtenYear == 1) {
                        String queryString = getQueryString(false, writtenDay, 1, writtenMonth, 1, writtenYear, todaysYear, code);
                        writeRows(code, queryString, printWriter);
                        queryString = getQueryString(true, 1, todaysDay, 1, todaysMonth, todaysYear, todaysYear, code);
                        writeRows(code, queryString, printWriter);

                    } else {
                        String queryString = getQueryString(true, writtenDay, todaysDay, writtenMonth, todaysMonth, writtenYear, todaysYear, code);
                        writeRows(code, queryString, printWriter);
                    }

                    codeDateMap.put(code, getLastDate(reader));
                    printWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return codeDateMap;
    }
}
