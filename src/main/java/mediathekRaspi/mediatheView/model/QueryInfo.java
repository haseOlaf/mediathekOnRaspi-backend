package mediathekRaspi.mediatheView.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class QueryInfo implements Serializable {
    private int resultCount;
    private int totalResults;

    public QueryInfo() {
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryInfo)) return false;
        QueryInfo queryInfo = (QueryInfo) o;
        return resultCount == queryInfo.resultCount &&
                totalResults == queryInfo.totalResults;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultCount, totalResults);
    }

    @Override
    public String toString() {
        return "QueryInfo{" +
                ", resultCount=" + resultCount +
                ", totalResults=" + totalResults +
                '}';
    }
}
