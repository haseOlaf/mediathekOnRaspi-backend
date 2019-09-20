package mediathekRaspi.mediatheView.resource;

import mediathekRaspi.mediatheView.model.Video;
import mediathekRaspi.mediatheView.service.MediatheViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/search")
public class MediathekViewResource {

    @Autowired
    MediatheViewService mediathekViewService;

    @GetMapping("/")
    public List<Video> getVideos(@RequestParam Optional<String> channel, @RequestParam Optional<String> title, @RequestParam Optional<String> topic) {
        return this.mediathekViewService.getVideos(channel.orElse(""), title.orElse(""), topic.orElse(""));
    }

}
