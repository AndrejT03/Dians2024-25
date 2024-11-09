package filters.impl;

import filters.IFilter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;

public class IssuersFilter implements IFilter<String, String> {
    private static final String url = "https://www.mse.mk/mk/stats/symbolhistory/kmb";

    @Override
    public Map<String, String> execute(Map<String, String> issuers) throws IOException {
        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.select(".dropdown option");
        for(Element el : elements) {
            String code = el.text();
            if(!code.matches(".*\\d.*")) {
                issuers.put(code, "");
            }
        }

        return issuers;
    }
}
