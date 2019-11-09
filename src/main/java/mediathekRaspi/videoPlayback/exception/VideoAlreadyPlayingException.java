package mediathekRaspi.videoPlayback.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class VideoAlreadyPlayingException extends RuntimeException{
    public VideoAlreadyPlayingException(String message) {
        super(message);
    }
}
