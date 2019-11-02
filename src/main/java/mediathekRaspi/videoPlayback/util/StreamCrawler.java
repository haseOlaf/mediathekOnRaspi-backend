package mediathekRaspi.videoPlayback.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class StreamCrawler {

    private static Logger LOG = LoggerFactory.getLogger(StreamCrawler.class);

    @Async
    public void crawl(InputStream in, Consumer<String> onNext, BooleanSupplier doStop, Supplier<String> logOnExit) {
        byte[] tmp = new byte[1024];

        while (true) {
            try {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    String readString = new String(tmp, 0, i);
                    onNext.accept(readString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (doStop.getAsBoolean()) {
                try {
                    if (in.available() > 0) continue;
                    LOG.info(logOnExit.get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
                LOG.error("Sleeping failed");
            }
        }
    }

}