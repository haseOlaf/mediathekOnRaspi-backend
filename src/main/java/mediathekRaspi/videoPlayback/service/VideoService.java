package mediathekRaspi.videoPlayback.service;

import mediathekRaspi.videoPlayback.exception.VideoAlreadyPlayingException;
import mediathekRaspi.videoPlayback.exception.VideoNotPlayingException;
import mediathekRaspi.videoPlayback.model.InteractiveShellProcess;
import mediathekRaspi.videoPlayback.model.SshSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VideoService {

    private InteractiveShellProcess playerProcess;
    Logger LOG = LoggerFactory.getLogger(VideoService.class);

    @Value("${ssh.user}")
    private String userName;

    @Value("${ssh.pw}")
    private String pw;

    @Value("${video.player}")
    private String videoPlayer;

    @Autowired
    SshSession sshSession;

    public VideoService() {
    }

    public void stop() {
        if (!videoIsPLaying()) {
            throw new VideoNotPlayingException("Es spielt gerade kein Video");
        }
        LOG.info("Stop video");
        playerProcess.write("q");
    }

    public void pause() {
        if (!videoIsPLaying()) {
            throw new VideoNotPlayingException("Es spielt gerade kein Video");
        }
        LOG.info("Pause video");
        playerProcess.write("p");
    }

    public String play(String video) {
        if (videoIsPLaying()) {
            throw new VideoAlreadyPlayingException(
                    "Es läuft schon ein Video. Es kann nur ein Video zur selben Zeit laufen.");
        }
        LOG.info("Play video " + video);
        connectToRaspi();
        playerProcess = new InteractiveShellProcess("interactiveShellProcess");
        String command = "sh -c 'echo $$; exec " + videoPlayer + " " + video + "'";
        playerProcess.writeln(command);
        return "playing " + video;
    }

    public void forward() {
        if (!videoIsPLaying()) {
            throw new VideoNotPlayingException("Es spielt gerade kein Video");
        }
        LOG.info("Forward");
        playerProcess.write("e");
    }

    public void rewind() {
        if (!videoIsPLaying()) {
            throw new VideoNotPlayingException("Es spielt gerade kein Video");
        }
        LOG.info("Rewind");
        playerProcess.write("w");
    }

    public void fastForward() {
        if (!videoIsPLaying()) {
            throw new VideoNotPlayingException("Es spielt gerade kein Video");
        }
        LOG.info("FastForward");
        playerProcess.write("t");
    }

    public void fastRewind() {
        if (!videoIsPLaying()) {
            throw new VideoNotPlayingException("Es spielt gerade kein Video");
        }
        LOG.info("FastRewind");
        playerProcess.write("r");
    }

    public void run(String command) {
        playerProcess.write(command);
    }

    private boolean videoIsPLaying() {
        return playerProcess != null && playerProcess.isProcessRunning();
    }

    private void connectToRaspi() {
        sshSession.setSession(userName, pw);
    }
}
