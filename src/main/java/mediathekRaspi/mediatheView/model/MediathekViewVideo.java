package mediathekRaspi.mediatheView.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class MediathekViewVideo implements Serializable {
    private String channel;
    private String topic;
    private String title;
    private String description;
    private int timestamp;
    private int duration;
    private int size;
    private String url_website;
    private String url_subtitle;
    private String url_video;
    private String url_video_low;
    private String url_video_hd;
    private int filmlisteTimestamp;

    public MediathekViewVideo() {
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUrl_website() {
        return url_website;
    }

    public void setUrl_website(String url_website) {
        this.url_website = url_website;
    }

    public String getUrl_subtitle() {
        return url_subtitle;
    }

    public void setUrl_subtitle(String url_subtitle) {
        this.url_subtitle = url_subtitle;
    }

    public String getUrl_video() {
        return url_video;
    }

    public void setUrl_video(String url_video) {
        this.url_video = url_video;
    }

    public String getUrl_video_low() {
        return url_video_low;
    }

    public void setUrl_video_low(String url_video_low) {
        this.url_video_low = url_video_low;
    }

    public String getUrl_video_hd() {
        return url_video_hd;
    }

    public void setUrl_video_hd(String url_video_hd) {
        this.url_video_hd = url_video_hd;
    }

    public int getFilmlisteTimestamp() {
        return filmlisteTimestamp;
    }

    public void setFilmlisteTimestamp(int filmlisteTimestamp) {
        this.filmlisteTimestamp = filmlisteTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediathekViewVideo)) return false;
        MediathekViewVideo that = (MediathekViewVideo) o;
        return duration == that.duration &&
                size == that.size &&
                Objects.equals(channel, that.channel) &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(url_website, that.url_website) &&
                Objects.equals(url_subtitle, that.url_subtitle) &&
                Objects.equals(url_video, that.url_video) &&
                Objects.equals(url_video_low, that.url_video_low) &&
                Objects.equals(url_video_hd, that.url_video_hd) &&
                Objects.equals(filmlisteTimestamp, that.filmlisteTimestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, topic, title, description, timestamp, duration, size, url_website, url_subtitle, url_video, url_video_low, url_video_hd, filmlisteTimestamp);
    }

    @Override
    public String toString() {
        return "MediathekViewVideo{" +
                "channel='" + channel + '\'' +
                ", topic='" + topic + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", timestamp=" + timestamp +
                ", duration=" + duration +
                ", size=" + size +
                ", url_website='" + url_website + '\'' +
                ", url_subtitle='" + url_subtitle + '\'' +
                ", url_video='" + url_video + '\'' +
                ", url_video_low='" + url_video_low + '\'' +
                ", url_video_hd='" + url_video_hd + '\'' +
                ", filmlisteTimestamp=" + filmlisteTimestamp +
                '}';
    }
}
