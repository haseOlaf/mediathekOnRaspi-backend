package mediathekRaspi.videoPlayback.util;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import mediathekRaspi.util.BeanService;
import mediathekRaspi.videoPlayback.model.InteractiveShellProcess;
import mediathekRaspi.videoPlayback.model.ShellConnection;

public class ShellUtil {

    StreamCrawler streamCrawler = BeanService.getBean(StreamCrawler.class);
    Logger LOG = LoggerFactory.getLogger(ShellUtil.class);

    @Async
    public void observeConnection(InteractiveShellProcess connection, ShellConnection observerConnection, Runnable observe, Consumer<String> onNext, BooleanSupplier doStop, Runnable onExit ) {
       LOG.debug("observeConnection startet");
       
        try {
            Thread.sleep(5);
        }
        catch(Exception e) {
        }

        LOG.debug(connection.getName() + ": start crawling observerConnection");
        streamCrawler.crawl(observerConnection.getInputStream(), onNext, doStop, onExit);

        observe.run();
    }
}