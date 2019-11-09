package mediathekRaspi.videoPlayback.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Component
public class StreamCrawler {

    private static Logger LOG = LoggerFactory.getLogger(StreamCrawler.class);

    @Async
    public void crawl(InputStream in, Consumer<String> onNext, BooleanSupplier doStop, Runnable onExit) {
        byte[] tmp = new byte[1024];

        while (true) {
            try {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 512);
                    if (i < 0) break;
                    String readString = new String(tmp, 0, i);
                    onNext.accept(readString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (doStop.getAsBoolean()) {
                try {
                    if (in.available() > 0) {
                        continue;
                    }
                    onExit.run();
                    in.close();
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
