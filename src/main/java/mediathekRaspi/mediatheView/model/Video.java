package mediathekRaspi.mediatheView.model;

import java.io.Serializable;
import java.util.Objects;

public class Video implements Serializable {
    private String title;
    private String channel;
    private int broadcastDate;
    private int duration;
    private String description;
    private String videoUrl;
    private String subtitleUrl;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getSubtitleUrl() {
        return subtitleUrl;
    }

    public void setSubtitleUrl(String subtitleUrl) {
        this.subtitleUrl = subtitleUrl;
    }

    public Video() {
    }

    public Video(String title, String channel, int broadcastDate, int duration, String videoUrl, String description, String subtitleUrl) {
        this.title = title;
        this.channel = channel;
        this.broadcastDate = broadcastDate;
        this.duration = duration;
        this.videoUrl = videoUrl;
        this.description = description;
        this.subtitleUrl = subtitleUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getBroadcastDate() {
        return broadcastDate;
    }

    public void setBroadcastDate(int broadcastDate) {
        this.broadcastDate = broadcastDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Video)) return false;
        Video video = (Video) o;
        return duration == video.duration &&
                Objects.equals(title, video.title) &&
                Objects.equals(channel, video.channel) &&
                Objects.equals(broadcastDate, video.broadcastDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, channel, broadcastDate, duration);
    }

    @Override
    public String toString() {
        return "Video{" +
                "title='" + title + '\'' +
                ", channel='" + channel + '\'' +
                ", broadcastDate=" + broadcastDate +
                ", duration=" + duration +
                '}';
    }
}
