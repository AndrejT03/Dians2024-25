package filters.impl;

import filters.IFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WriteFilter implements IFilter<String, String> {
    static final String url = "https://www.mse.mk/mk/stats/symbolhistory/";

    @Override
    public Map<String, String> execute(Map<String, String> map) throws IOException {
        return new HashMap<>();
    }

    public void writeHeader(String code, PrintWriter printWriter) throws IOException {
        Document doc = Jsoup.connect(String.format("%s%s", url, code)).get();
        Elements headerElements = doc.select(".thead-default tr th");
        headerElements.stream()
                .filter(e -> e != headerElements.getLast())
                .forEach(e -> printWriter.printf("\"%s\",", e.text()));
        printWriter.printf("\"%s\"\n", headerElements.getLast().text());
    }

    public void writeRows(String code, String queryString, PrintWriter printWriter) throws IOException {
        Document doc = Jsoup.connect(String.format("%s%s%s", url, code, queryString)).get();
        Elements rowElements = doc.select("#resultsTable tbody tr");
        rowElements.reversed().forEach(row -> {
            Elements infoElements = row.select("td");
            infoElements.stream()
                    .filter(info -> info != infoElements.getLast())
                    .forEach(info -> printWriter.printf("\"%s\",", info.text()));

            printWriter.printf("\"%s\"\n", infoElements.getLast().text());
        });
    }

    public String getTodaysFormattedDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return today.format(formatter);
    }

    public String getQueryString(boolean lastCycle, int fromDay, int toDay, int fromMonth, int toMonth, int fromYear, int toYear, String code) {
        String startingFormattedDate = String.format("%d.%d.%d", fromDay, fromMonth, fromYear);
        if(lastCycle) {
            return String.format("?FromDate=%s&ToDate=%s&Code=%s", startingFormattedDate, getTodaysFormattedDate(), code);
        }
        else {
            String endingFormattedDate = String.format("%d.%d.%d", toDay, toMonth, toYear);
            return String.format("?FromDate=%s&ToDate=%s&Code=%s", startingFormattedDate, endingFormattedDate, code);
        }
    }

    public String getLastDate(BufferedReader reader) throws IOException {
        String line;
        String lastLine = "";

        while((line = reader.readLine()) != null && !line.isEmpty()) {
            lastLine = line;
        }

        String [] elements = lastLine.split(",");
        return elements[0].substring(1, elements[0].length() - 1);
    }

}
