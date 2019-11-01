package mediathekRaspi.videoPlayback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class NotConnectedException extends  RuntimeException {
    public NotConnectedException(String message) {
        super(message);
    }
}
