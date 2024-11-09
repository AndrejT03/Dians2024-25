package pipe;

import filters.IFilter;
import filters.impl.DateFilter;
import filters.impl.InformationFilter;
import filters.impl.IssuersFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pipe {
    Map<String, String> codesAndDates;
    List<IFilter<String, String>> filters;

    public Pipe() {
        this.codesAndDates = new HashMap<>();
        this.filters = new ArrayList<>();
    }
    public void createFilters() {
        filters.add(new IssuersFilter());
        filters.add(new DateFilter());
        filters.add(new InformationFilter());
    }

    public void executeFilters() throws IOException {
        for(IFilter<String, String> filter : filters) {
            this.codesAndDates = filter.execute(this.codesAndDates);
        }
    }

}
