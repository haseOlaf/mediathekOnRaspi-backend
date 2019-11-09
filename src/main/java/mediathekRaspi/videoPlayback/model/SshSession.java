package mediathekRaspi.videoPlayback.model;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import mediathekRaspi.videoPlayback.exception.NotConnectedException;

@Component
public class SshSession {

    private static Session session;
    private Logger LOG = LoggerFactory.getLogger(SshSession.class);

    public Session getSession() {
        return session;
    }
    
    public SshSession() {}

    public void setSession(String username, String password) {
        LOG.info("user: " + username + " tries to connect via ssh");
        LOG.debug("pw: " + password);

        try {
            JSch jSch = new JSch();
            session = jSch.getSession(username, "localhost", 22);
            // fixme only allow known hostKeys
            // jSch.setKnownHosts(new ByteArrayInputStream(knownHostPublicKey.getBytes()));
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect(120000);
        } catch (Exception e) {
            LOG.error("Konnte nicht auf den raspi verbinden.");
            throw new NotConnectedException("Konnte nicht auf den raspi verbinden.");
        }
    }

    public void disposeSession() {
        if (session == null) {
            return;
        }
        if (session.isConnected()) {
            LOG.info("closing session");
            session.disconnect();
        }
        session = null;
    }

}