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

    @RequestMapping("start")
    public String start() {
        videoService.start();
        return "start";
    }

    @RequestMapping("ls")
    public void ls() {
        videoService.writeln("ls");
    }

    @RequestMapping("less")
    public void less() {
        videoService.writeln("less subs.srt");
    }


    @RequestMapping("quit")
    public void quit() {
        videoService.write("q");
    }

    @RequestMapping("pause")
    public void pause() {
        videoService.write("p");
    }

    @RequestMapping("exit")
    public void exit() {
        videoService.exit();
    }

    @RequestMapping(value = "run", method = RequestMethod.GET)
    public void run(@RequestParam("command") String command) {
        LOG.info("command: " + command);
        videoService.write(command);
    }

    @RequestMapping(value = "play", method = RequestMethod.GET)
    public void play(@RequestParam("video") String video) {
        if (video == null || video == "") video = "https://download.media.tagesschau.de/video/2019/0730/TV-20190730-2022-5001.h264.mp4";
        videoService.play(video);
    }

}