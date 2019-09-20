package mediathekRaspi.mediatheView.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Filter implements Serializable {

    private List<String> fields;

    private String query;

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Filter() {
    }

    @Override
    public String toString() {
        return "Filter{" +
                "fields=" + fields +
                ", query='" + query + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Filter)) return false;
        Filter queries = (Filter) o;
        return Objects.equals(fields, queries.fields) &&
                Objects.equals(query, queries.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields, query);
    }
}
