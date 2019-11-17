package mediathekRaspi.videoPlayback.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mediathekRaspi.util.BeanService;
import mediathekRaspi.util.Sleeper;
import mediathekRaspi.util.StringUtil;
import mediathekRaspi.videoPlayback.util.StreamCrawler;

public class InteractiveShellProcess extends ShellConnection {

    private int activePid;
    private ShellConnection observerConnection;
    private Logger LOG = LoggerFactory.getLogger(InteractiveShellProcess.class);
    private StreamCrawler streamCrawler = BeanService.getBean(StreamCrawler.class);

    public InteractiveShellProcess(String name, String command) {
        super(name);
        startInteractiveProcess(command);
    }

    private void startInteractiveProcess(String command) {
        LOG.debug(getName() + ": start crawling interactivShellProcess");
        streamCrawler.crawl(getInputStream(), this::onNext, this::isClosed, this::onExit);
        Sleeper.sleep(1000);
        writeln("sh -c 'echo $$; exec " + command + "'");
    }

    @Override
    public void disconnect() {
        writeln("exit");
        Sleeper.sleep(1000);
        activePid = 0;
        super.disconnect();
    }

    public boolean isProcessRunning() {
        return activePid != 0;
    }

    private void onNext(String readString) {
        if (StringUtil.isEmpty(readString)) {
            return;
        }
        LOG.debug(getName() + ": " + readString);

        if (isProcessRunning()) {
            return;
        }

        detectStartOfProcess(readString);
        if (isProcessRunning()) {
            getSession().observeProcess(this, this::waitForProcessEnd);
        }
    }

    private void onExit() {
        LOG.info(getName() + ": exit-status: " + getExitStatus());
        dispose();
        getSession().disposeSession();
    }

    private void detectStartOfProcess(String readString) {
        Pattern pidPattern = Pattern.compile("^[0-9]+$", Pattern.MULTILINE);
        Matcher pidMatcher = pidPattern.matcher(readString);
        if (pidMatcher.find()) {
            activePid = Integer.parseInt(pidMatcher.group());
            LOG.info(getName() + ": started process with pid " + activePid);
        }
    }

    private void waitForProcessEnd(ShellConnection connection) {
        observerConnection = connection;
        LOG.debug("observeConnection startet");

        Sleeper.sleep(5000);

        streamCrawler.crawl(observerConnection.getInputStream(), this::onNextObservation, observerConnection::isClosed,
                this::onObservationFinished);

        Sleeper.sleep(5000);

        while (true) {
            if (observerConnection.isClosed()) {
                break;
            }
            queryProcessState();
            Sleeper.sleep(5000);
        }
    }

    private void queryProcessState() {
        observerConnection.writeln("ps -o pid= -p " + activePid + ">/dev/null && echo \"" + activePid
                + "running\" || echo \"" + activePid + "notRunning\"");
    }

    private void onNextObservation(String readString) {
        if (StringUtil.isEmpty(readString)) {
            return;
        }
        LOG.debug("observerConnection: " + readString);
        if (obervedProcessStopped(readString)) {
            disconnectAllConnections();
        }
    }

    private boolean obervedProcessStopped(String readString) {
        Pattern pattern = Pattern.compile("^" + activePid + "notRunning$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(readString);
        return matcher.find();
    }

    private void disconnectAllConnections() {
        LOG.info("oberserConnection: " + activePid + " not running anymore. Closing connections");
        observerConnection.disconnect();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }
        disconnect();
    }

    private void onObservationFinished() {
        LOG.info(getName() + ": stop observing pid " + activePid);
        observerConnection.dispose();
    }
}