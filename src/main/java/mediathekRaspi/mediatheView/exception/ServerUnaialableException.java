package mediathekRaspi.mediatheView.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class ServerUnaialableException extends RuntimeException {
    public ServerUnaialableException() {
        super("Server not available");
    }
}
