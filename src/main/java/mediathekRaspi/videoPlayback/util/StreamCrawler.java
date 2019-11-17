package mediathekRaspi.videoPlayback.util;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import mediathekRaspi.util.Sleeper;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Component
public class StreamCrawler {

    @Async
    public void crawl(InputStream in, Consumer<String> onNext, BooleanSupplier doStop, Runnable onExit) {
        byte[] tmp = new byte[1024];

        while (true) {
            try {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 512);
                    if (i < 0) {
                        break;
                    }
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
                    in.close();
                    onExit.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            Sleeper.sleep(1000);
        }
    }
}
