package mediathekRaspi.mediatheView.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class MediathekViewAnswer implements Serializable {
    private MediathekViewResult result;

    private List<String> err;

    public MediathekViewAnswer() {
    }

    public MediathekViewResult getResult() {
        return result;
    }

    public void setResult(MediathekViewResult result) {
        this.result = result;
    }

    public List<String> getErr() {
        return err;
    }

    public void setErr(List<String> err) {
        this.err = err;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediathekViewAnswer)) return false;
        MediathekViewAnswer that = (MediathekViewAnswer) o;
        return Objects.equals(result, that.result) &&
                Objects.equals(err, that.err);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, err);
    }

    @Override
    public String toString() {
        return "MediathekViewAnswer{" +
                "result=" + result +
                ", err=" + err +
                '}';
    }
}
