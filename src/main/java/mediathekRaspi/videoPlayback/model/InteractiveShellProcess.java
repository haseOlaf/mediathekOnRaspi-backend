package mediathekRaspi.videoPlayback.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mediathekRaspi.util.BeanService;
import mediathekRaspi.videoPlayback.util.ShellUtil;
import mediathekRaspi.videoPlayback.util.StreamCrawler;

public class InteractiveShellProcess extends ShellConnection {

    private int activePid;
    private ShellConnection observerConnection;
    private Logger LOG = LoggerFactory.getLogger(InteractiveShellProcess.class);
    private StreamCrawler streamCrawler = BeanService.getBean(StreamCrawler.class);

    public InteractiveShellProcess(String name) {
        super(name);

        LOG.debug(getName() + ": start crawling interactivShellProcess");
        streamCrawler.crawl(getInputStream(), this::setActivePidFromStream, this::isClosed, this::onExit);
        try {
            Thread.sleep(1000);
        } catch (Exception ee) {
        }
        LOG.debug(getName() + ": constructor finished");
    }

    public int getActivePid() {
        return activePid;
    }

    public void setActivePid(int pid) {
        this.activePid = pid;
        LOG.info(getName() + ": started process with pid " + activePid);
    }

    private void onExit() {
        LOG.info(getName() + ": exit-status: " + getExitStatus());
        dispose();
        getSession().disposeSession();
    }

    private void setActivePidFromStream(String readString) {
        if (readString.isEmpty()) {
            return;
        }

        LOG.debug(getName() + ": " + readString);

        if (getActivePid() == 0) {
            Pattern pidPattern = Pattern.compile("^[0-9]+$", Pattern.MULTILINE);
            Matcher pidMatcher = pidPattern.matcher(readString);
            if (pidMatcher.find()) {
                setActivePid(Integer.parseInt(pidMatcher.group()));
                LOG.debug(getName() + ": start observing process");
                ShellUtil shellUtil = new ShellUtil();
                observerConnection = new ShellConnection("observerShell");
                shellUtil.observeConnection(this, observerConnection,
                        () -> continouslyQueryProcessState(observerConnection),
                        it -> this.closeConnectionIfPidIsInactive(it, observerConnection), observerConnection::isClosed,
                        () -> {
                            LOG.info(getName() + ": stop observing pid " + getActivePid());
                            observerConnection.dispose();
                        });
            }
        }
    }

    public void continouslyQueryProcessState(ShellConnection observerConnection) {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }
        while (true) {
            if (observerConnection.isClosed()) {
                break;
            }

            observerConnection.writeln("ps -o pid= -p " + activePid + ">/dev/null && echo \"" + activePid
                    + "running\" || echo \"" + activePid + "notRunning\"");

            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
        }
    }

    public void closeConnectionIfPidIsInactive(String readString, ShellConnection observerConnection) {
        LOG.debug("observerConnection: " + readString);
        Pattern pattern = Pattern.compile("^" + activePid + "notRunning$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(readString);
        if (matcher.find()) {
            LOG.info("oberserConnection: " + activePid + " not running anymore. Closing connections");
            observerConnection.disconnect();
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
            disconnect();
        }
    }

}