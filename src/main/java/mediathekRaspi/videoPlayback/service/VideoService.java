package mediathekRaspi.videoPlayback.service;

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
        LOG.info("Stop video");
        playerProcess.write("q");
    }

    public void pause() {
        LOG.info("Pause video");
        playerProcess.write("p");
    }

    public String play(String video) {
        LOG.info("Play video " + video);
        connectToRaspi();
        playerProcess = new InteractiveShellProcess("interactiveShellProcess");
        String command = "sh -c 'echo $$; exec " + videoPlayer + " " + video + "'";
        playerProcess.writeln(command);
        return "playing " + video;
    }

    public void forward() {
        LOG.info("Forward");
        playerProcess.write("e");
    }

    public void rewind() {
        LOG.info("Rewind");
        playerProcess.write("w");
    }

    public void fastForward() {
        LOG.info("FastForward");
        playerProcess.write("t");
    }

    public void fastRewind() {
        LOG.info("FastRewind");
        playerProcess.write("r");
    }

    public void run(String command) {
        playerProcess.write(command);
    }

    private void connectToRaspi() {
        sshSession.setSession(userName, pw);
    }
}
