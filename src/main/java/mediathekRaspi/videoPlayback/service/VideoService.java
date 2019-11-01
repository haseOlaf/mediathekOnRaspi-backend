//-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000

package mediathekRaspi.videoPlayback.service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import mediathekRaspi.videoPlayback.exception.NotConnectedException;
import mediathekRaspi.videoPlayback.util.MediathekRaspiStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class VideoService {

    private OutputStream outputStream;
    private Session session;
    private Channel channel;
    Logger LOG = LoggerFactory.getLogger(VideoService.class);

    @Value("${ssh.user}")
    private String userName;

    @Value("${ssh.pw}")
    private String pw;

    @Value("${video.player}")
    private String videoPlayer;

    @Autowired
    MediathekRaspiStreamHandler streamHandler;

    public VideoService() {
    }

    public Channel getChannel() {
        return channel;
    }

    public int connect() {
        LOG.info("user: " + userName + " tries to connect via ssh");
        LOG.debug("pw: " + pw);

        try {
            JSch jSch = new JSch();
            session = jSch.getSession(userName, "localhost", 22);
            //fixme only allow known hostKeys
            //  jSch.setKnownHosts(new ByteArrayInputStream(knownHostPublicKey.getBytes()));
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(pw);
            session.connect(120000);
            channel = session.openChannel("shell");
            InputStream inputStream = channel.getInputStream();
            outputStream = channel.getOutputStream();
            channel.connect();
            if (outputStream == null) LOG.error("outputStream null");
            listenToInputStream(inputStream);
            return 0;
        } catch (Exception e) {
            LOG.error("Konnte nicht auf den raspi verbinden.");
            throw new NotConnectedException("Konnte nicht auf den raspi verbinden.");
        }
    }

    public void disconnect() {
        write("exit");
        try {
            Thread.sleep(1000);
        } catch (Exception ee) {
        }
        channel.disconnect();
        session.disconnect();
    }

    public void stop() {

        write("q");
        disconnect();
    }

    public void pause() {
        write("p");
    }

    public String play(String video) {
        connect();
	try {
            Thread.sleep(1000);
        } catch (Exception ee) {
        }
        String command = videoPlayer + " " + video;
        LOG.info("command: " + command);
        writeln(command);
        return "playing " + video;
    }

    public void forward() {
        write("e");
    }

    public void rewind() {
        write("w");
    }

    public void fastForward() {
        write("t");
    }

    public void fastRewind() {
        write("r");
    }

    public void run(String command) {
        write(command);
    }

    private void write(String command) {
        if (outputStream == null) LOG.error("outputStream null");
        if (command == null) LOG.error("command null");
        LOG.info("run " + command);
        try {
            outputStream.write((command).getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeln(String command) {
        write(command + "\n");
    }

    private void listenToInputStream(InputStream in) {
        streamHandler.listenToInputStream(in, this);
    }
}
