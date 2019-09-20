package mediathekRaspi.mediatheView.model;

import java.io.Serializable;
import java.util.List;

public class MediathekViewResult implements Serializable {

    private List<MediathekViewVideo> results;

    private QueryInfo queryInfo;

    public List<MediathekViewVideo> getResults() {
        return results;
    }

    public void setResults(List<MediathekViewVideo> results) {
        this.results = results;
    }
}
