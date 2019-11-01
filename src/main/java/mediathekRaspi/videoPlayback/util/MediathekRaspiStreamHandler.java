package mediathekRaspi.videoPlayback.util;

import mediathekRaspi.videoPlayback.service.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MediathekRaspiStreamHandler {

    private static Logger LOG = LoggerFactory.getLogger(MediathekRaspiStreamHandler.class);

    @Async
    public void listenToInputStream(InputStream in, VideoService videoService) {
        byte[] tmp = new byte[1024];

        while (true) {
            try {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    String readString = new String(tmp, 0, i);
                    LOG.debug(readString);
                    if (!readString.isEmpty() && readString.contains("Thank you")) {
                        LOG.info("Video ended. Disconnecting from raspi");
                        videoService.disconnect();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (videoService.getChannel().isClosed()) {
                try {
                    if (in.available() > 0) continue;
                    LOG.debug("exit-status: " + videoService.getChannel().getExitStatus());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                LOG.error("Sleeping failed");
            }
        }
    }
}
