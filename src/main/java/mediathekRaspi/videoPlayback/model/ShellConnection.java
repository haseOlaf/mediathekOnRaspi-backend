package mediathekRaspi.videoPlayback.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mediathekRaspi.util.BeanService;
import mediathekRaspi.util.Sleeper;
import mediathekRaspi.videoPlayback.exception.NotConnectedException;

public class ShellConnection {
    private OutputStream outputStream;
    private SshSession session = BeanService.getBean(SshSession.class);

    private Channel channel;
    private String name;
    private Logger LOG = LoggerFactory.getLogger(ShellConnection.class);

    public ShellConnection(String name) {
        this.name = name;
        openChannel();
    }

    public String getName() {
        return name;
    }

    public SshSession getSession() {
        return this.session;
    }

    public InputStream getInputStream() {
        try {
            return channel.getInputStream();
        } catch (Exception e) {
            throw new NotConnectedException(name + ": kein Inputstream da");
        }
    }

    public boolean isClosed() {
        return channel == null || channel.isClosed();
    }

    public int getExitStatus() {
        int exitStatus = channel.getExitStatus();
        LOG.debug(name + ": exit status " + exitStatus);
        return exitStatus;
    }

    public void write(String command) {
        if (outputStream == null)
            LOG.error(name + ": outputStream null");
        if (command == null)
            LOG.error("command null");
        LOG.debug(name + ": run " + command);
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
        LOG.debug(getName() + ": disconnecting");
        if (channel != null && channel.isConnected()) {
            LOG.info(name + ": closing shellConnection");
            channel.disconnect();
        }
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(name + ": Error while closing OutputStream");
        }
        Sleeper.sleep(2000);
    }

    public void dispose() {
        LOG.info(name + ": disposing connection");
        channel = null;
    }

    private void openChannel() {
        try {
            channel = session.getSession().openChannel("shell");
            outputStream = channel.getOutputStream();
            channel.connect();
            if (outputStream == null) {
                LOG.error(name + ": outputStream null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotConnectedException(name + ": konnte keine shell öffnen");
        }
        if (channel == null) {
            throw new NotConnectedException(name + ": konnte keine shell öffnen");
        }
        LOG.debug(getName() + ": opened Connection");
    }
}