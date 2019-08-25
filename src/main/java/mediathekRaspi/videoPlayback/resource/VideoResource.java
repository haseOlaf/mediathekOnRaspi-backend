package mediathekRaspi.videoPlayback.resource;

import mediathekRaspi.videoPlayback.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class VideoResource {

    Logger LOG = LoggerFactory.getLogger(VideoResource.class);

    @Autowired
    VideoService videoService;

    @RequestMapping("connect")
    public String connect() {
        videoService.connect();
        return "start";
    }

    @RequestMapping("stop")
    public void stop() {
        videoService.stop();
    }

    @RequestMapping("pause")
    public void pause() {
        videoService.pause();
    }

    @RequestMapping("forward")
    public void forward() { videoService.forward(); }

    @RequestMapping("rewind")
    public void rewind() { videoService.rewind(); }

    @RequestMapping("fastforward")
    public void fastForward() { videoService.fastForward(); }

    @RequestMapping("fastrewind")
    public void fastRewind() { videoService.fastRewind(); }

    @RequestMapping("disconnect")
    public void disconnect() {
        videoService.disconnect();
    }

    @RequestMapping(value = "run", method = RequestMethod.GET)
    public void run(@RequestParam("command") String command) {
        videoService.run(command);
    }

    @RequestMapping(value = "play", method = RequestMethod.GET)
    public void play(@RequestParam("video") String video) {
        if (video == null || video == "") video = "https://download.media.tagesschau.de/video/2019/0730/TV-20190730-2022-5001.h264.mp4";
        videoService.play(video);
    }

}