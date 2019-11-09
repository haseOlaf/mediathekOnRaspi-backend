package mediathekRaspi.videoPlayback.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;

import mediathekRaspi.util.BeanService;
import mediathekRaspi.videoPlayback.exception.NotConnectedException;
import mediathekRaspi.videoPlayback.util.StreamCrawler;

public class ShellConnection {
    private OutputStream outputStream;
    private Session session;
    private StreamCrawler streamCrawler = BeanService.getBean(StreamCrawler.class);
    private int activePid;
    private Channel channel;
    private Logger LOG = LoggerFactory.getLogger(ShellConnection.class);

    public ShellConnection(Session session) {
        this.session = session;
        openChannel();
    }

    public int getActivePid() {
        return activePid;
    }

    public void setActivePid(int pid) {
        this.activePid = pid;
        LOG.info("started process with pid " + activePid);
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    private void openChannel() {
        try {
            channel = session.openChannel("shell");
            outputStream = channel.getOutputStream();
            channel.connect();
            if (outputStream == null) {
                LOG.error("outputStream null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotConnectedException("konnte keine shell öffnen");
        }
        if (channel == null) {
            throw new NotConnectedException("konnte keine shell öffnen");
        }
    }

    public InputStream getInputStream() {
        try {
            return channel.getInputStream();
        } catch (Exception e) {
            throw new NotConnectedException("kein Inputstream da");
        }
    }

    public boolean isClosed() {
        return channel.isClosed();
    }

    public int getExitStatus() {
        return channel.getExitStatus();
    }

    public void write(String command) {
        if (outputStream == null)
            LOG.error("outputStream null");
        if (command == null)
            LOG.error("command null");
        LOG.debug("run " + command);
        try {
            outputStream.write((command).getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeln(String command) {
        write(command + "\n");
    }

    public void disconnect() {
        if (channel.isConnected()) {
            writeln("exit");
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
            LOG.info("closing shellConnection");
            channel.disconnect();
        }
        try {
            outputStream.close();
            Thread.sleep(200);
        } catch (Exception e) {
            throw new RuntimeException("Error while closing OutputStream");
        }
    }

    public void dispose() {
        channel = null;
    }

    @Async
    public void closeOnFinishOfActivePid() {
        if (!channel.isConnected()) {
            disconnect();
            return;
        }

        ShellConnection observerConnection = new ShellConnection(session);

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }

        streamCrawler.crawl(observerConnection.getInputStream(), it -> observePid(it, observerConnection),
                observerConnection::isClosed, () -> LOG.info("stop observing pid " + activePid));

        while (true) {
            if (observerConnection.isClosed()) {
                break;
            }

            observerConnection.write("ps -o pid= -p " + activePid);
            observerConnection.write(">/dev/null");
            observerConnection.write(" && echo \"" + activePid + "running\"");
            observerConnection.write(" || echo \"" + activePid + "notRunning\" \n");

            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }
        }
    }

    private void observePid(String readString, ShellConnection observerConnection) {
        LOG.debug("oberserver Connection: " + readString);
        Pattern pattern = Pattern.compile("^" + activePid + "notRunning$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(readString);
        if (matcher.find()) {
            LOG.info(activePid + " not running anymore. Closing connections");
            LOG.debug("disconnecting oberserver Connection");
            observerConnection.disconnect();
            LOG.debug("disconnecting main Connection");
            disconnect();
        }
    }
}