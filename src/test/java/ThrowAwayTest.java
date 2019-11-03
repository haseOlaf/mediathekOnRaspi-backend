import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ThrowAwayTest {

    @Test
    public void test() {
        String text = "'s://download.media.tagesschau.de/video/2019/0730/TV-20190730-2022-5001.h264.mp4'\n" + "5338\n"
                + "VLC media player 3.0.8 Vetinari (revision 3.0.8-0-gf350b6b5a7)";
        Pattern pattern = Pattern.compile("[0-9]+\\n");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}