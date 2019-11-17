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
            if (endOfProcessDetected(readString)) {
                disconnect();
            } else {
                queryProcessState();
            }
        }

        detectStartOfProcess(readString);
        if (isProcessRunning()) {
            queryProcessState();
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

    private void queryProcessState() {
        writeln("ps -o pid= -p " + activePid + ">/dev/null && echo \"" + activePid + "running\" || echo \"" + activePid
                + "notRunning\"");
    }

    private boolean endOfProcessDetected(String readString) {
        Pattern pattern = Pattern.compile("^" + activePid + "notRunning$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(readString);
        return matcher.find();
    }
}