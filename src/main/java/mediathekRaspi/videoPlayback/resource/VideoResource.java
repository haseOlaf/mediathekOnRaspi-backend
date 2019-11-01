package mediathekRaspi.videoPlayback.resource;

import mediathekRaspi.videoPlayback.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/player")
public class VideoResource {

    Logger LOG = LoggerFactory.getLogger(VideoResource.class);

    @Autowired
    VideoService videoService;

    @GetMapping("connect")
    public String connect() {
        videoService.connect();
        return "start";
    }

    @GetMapping("stop")
    public void stop() {
        videoService.stop();
    }

    @GetMapping("pause")
    public void pause() {
        videoService.pause();
    }

    @GetMapping("forward")
    public void forward() { videoService.forward(); }

    @GetMapping("rewind")
    public void rewind() { videoService.rewind(); }

    @GetMapping("fastforward")
    public void fastForward() { videoService.fastForward(); }

    @GetMapping("fastrewind")
    public void fastRewind() { videoService.fastRewind(); }

    @GetMapping("disconnect")
    public void disconnect() {
        videoService.disconnect();
    }

    @GetMapping(value = "run")
    public void run(@RequestParam("command") String command) {
        videoService.run(command);
    }

    @GetMapping(value = "play")
    public void play(@RequestParam("video") String video) {
        if (video == null || video == "") {
            video = "https://download.media.tagesschau.de/video/2019/0730/TV-20190730-2022-5001.h264.mp4";
        }
        videoService.play(video);
    }

}
