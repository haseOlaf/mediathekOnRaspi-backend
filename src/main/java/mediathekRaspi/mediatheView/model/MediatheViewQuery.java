package mediathekRaspi.mediatheView.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class MediatheViewQuery implements Serializable {
    List<Filter> queries;
    String sortBy;
    String sortOrder;
    boolean future;
    int offset;
    int size;

    public MediatheViewQuery() {
    }

    public MediatheViewQuery(int pageNumber) {
        this.queries = queries;
        this.sortBy = "timestamp";
        this.sortOrder = "desc";
        this.future = false;
        this.size = 50;
        this.offset = pageNumber * this.size;
    }

    public List<Filter> getQueries() {
        return queries;
    }

    public void setQueries(List<Filter> queries) {
        this.queries = queries;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isFuture() {
        return future;
    }

    public void setFuture(boolean future) {
        this.future = future;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "MediatheViewQuery{" +
                "queries=" + queries +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                ", future=" + future +
                ", offset=" + offset +
                ", size=" + size +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediatheViewQuery)) return false;
        MediatheViewQuery that = (MediatheViewQuery) o;
        return future == that.future &&
                offset == that.offset &&
                size == that.size &&
                Objects.equals(queries, that.queries) &&
                Objects.equals(sortBy, that.sortBy) &&
                Objects.equals(sortOrder, that.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queries, sortBy, sortOrder, future, offset, size);
    }
}
