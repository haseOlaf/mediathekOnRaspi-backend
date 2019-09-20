package mediathekRaspi.mediatheView.model;

public class VideoMapper {

    public static Video map(MediathekViewVideo mediathekViewVideo) {
        return new Video(mediathekViewVideo.getTitle(),
                mediathekViewVideo.getChannel(),
                mediathekViewVideo.getTimestamp(),
                mediathekViewVideo.getDuration(),
                mediathekViewVideo.getUrl_video_hd(),
                mediathekViewVideo.getDescription(),
                mediathekViewVideo.getUrl_subtitle()
                );
    }
}
