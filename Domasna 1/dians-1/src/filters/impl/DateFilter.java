package filters.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.Map;

public class DateFilter extends WriteFilter{

    @Override
    public Map<String, String> execute(Map<String, String> codesAndDates) throws IOException {
        String todaysformattedDate = getTodaysFormattedDate();

        for(String code : codesAndDates.keySet()) {
            String fileName = code + ".csv";
            File file = new File(fileName);

            if(file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                    String lastDate = getLastDate(reader);
                    if(!lastDate.equals(todaysformattedDate)) {
                        codesAndDates.put(code, lastDate);
                    }
                    else {
                        codesAndDates.put(code, todaysformattedDate);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                FileWriter fileWriter = new FileWriter(fileName);
                PrintWriter printWriter = new PrintWriter(fileWriter);

                writeHeader(code, printWriter);
                String[] parts = todaysformattedDate.split("\\.");
                int todaysYear = Integer.parseInt(parts[2]);

                for(int i=todaysYear-10; i<todaysYear; i++) {
                    String queryString = getQueryString(false, 1, 1, 1, 1, i, i + 1, code);
                    writeRows(code, queryString, printWriter);
                }

                String queryString = getQueryString(true,1, 1, 1, 1, todaysYear, todaysYear+1, code);
                writeRows(code, queryString, printWriter);
                printWriter.flush();

                try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                    codesAndDates.put(code, getLastDate(reader));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                codesAndDates.put(code, todaysformattedDate);
            }
        }
        return codesAndDates;
    }
}
