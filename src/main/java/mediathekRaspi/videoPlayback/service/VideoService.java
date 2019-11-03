//-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000

package mediathekRaspi.videoPlayback.service;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import mediathekRaspi.videoPlayback.exception.NotConnectedException;
import mediathekRaspi.videoPlayback.util.StreamCrawler;
import mediathekRaspi.videoPlayback.model.ShellConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VideoService {

    private Session session;
    private ShellConnection mainConnection;
    Logger LOG = LoggerFactory.getLogger(VideoService.class);

    @Value("${ssh.user}")
    private String userName;

    @Value("${ssh.pw}")
    private String pw;

    @Value("${video.player}")
    private String videoPlayer;

    @Autowired
    StreamCrawler streamCrawler;

    public VideoService() {
    }

    private Session getSession() {
        LOG.info("user: " + userName + " tries to connect via ssh");
        LOG.debug("pw: " + pw);

        try {
            JSch jSch = new JSch();
            session = jSch.getSession(userName, "localhost", 22);
            // fixme only allow known hostKeys
            // jSch.setKnownHosts(new ByteArrayInputStream(knownHostPublicKey.getBytes()));
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(pw);
            session.connect(120000);
            return session;
        } catch (Exception e) {
            LOG.error("Konnte nicht auf den raspi verbinden.");
            throw new NotConnectedException("Konnte nicht auf den raspi verbinden.");
        }
    }

    private void closeSession() {
        if (session.isConnected()) {
            LOG.info("closing session");
            session.disconnect();
        }
    }

    public void disconnect() {
        writeln("exit");
        try {
            Thread.sleep(1000);
        } catch (Exception ee) {
        }
        mainConnection.disconnect();
        closeSession();
        mainConnection.setActivePid(0);
    }

    public void stop() {
        LOG.info("Stop video");
        write("q");
        disconnect();
    }

    public void pause() {
        LOG.info("Pause video");
        write("p");
    }

    public String play(String video) {
        LOG.info("Play video " + video);
        mainConnection = new ShellConnection(getSession(), streamCrawler);
        streamCrawler.crawl(mainConnection.getInputStream(), (in) -> onNextFromStream(in), () -> mainConnection.isClosed(),
                    () -> onExit());
        try {
            Thread.sleep(1000);
        } catch (Exception ee) {
        }
        String command = "sh -c 'echo $$; exec " + videoPlayer + " " + video + "'";
        writeln(command);
        return "playing " + video;
    }

    private String onExit() {
        closeSession();
        return "exit-status: " + mainConnection.getExitStatus();
    }

    public void forward() {
        LOG.info("Forward");
        write("e");
    }

    public void rewind() {
        LOG.info("Rewind");
        write("w");
    }

    public void fastForward() {
        LOG.info("FastForward");
        write("t");
    }

    public void fastRewind() {
        LOG.info("FastRewind");
        write("r");
    }

    public void run(String command) {
        write(command);
    }

    private void write(String command) {
        mainConnection.write(command);
    }

    private void writeln(String command) {
        write(command + "\n");
    }

    private void onNextFromStream(String readString) {
        if (readString.isEmpty()) {
            return;
        }

        if (mainConnection.getActivePid() == 0) {
            Pattern pidPattern = Pattern.compile("^[0-9]+$", Pattern.MULTILINE);
            Matcher pidMatcher = pidPattern.matcher(readString);
            if (pidMatcher.find()) {
                mainConnection.setActivePid(Integer.parseInt(pidMatcher.group()));
                mainConnection.closeOnFinishOfActivePid();
            } 
        }

        LOG.debug(readString);
    }
}
