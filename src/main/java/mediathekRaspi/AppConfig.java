package mediathekRaspi;

//import mediathekRaspi.videoPlayback.service.ShellService;
import mediathekRaspi.videoPlayback.service.VideoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class AppConfig {

    @Bean
    @Scope("singleton")
    public VideoService videoService() {
        return new VideoService();
    }
}
