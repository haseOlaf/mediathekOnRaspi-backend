//-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000

package mediathekRaspi.videoPlayback.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class VideoService {

    private OutputStream outputStream;
    private Session session;
    private Channel channel;
    Logger LOG = LoggerFactory.getLogger(VideoService.class);

    @Value("${ssh.user}")
    private String userName;

    @Value("${ssh.pw}")
    private String pw;

    @Value("${video.player}")
    private String videoPlayer;

    public VideoService() {}

    public int connect() {
        LOG.info("user: " + userName + " tries to connect via ssh");
        LOG.debug("pw: " + pw);

        try {
            JSch jSch = new JSch();
            session = jSch.getSession(userName, "localhost", 22);
            //fixme only allow known hostKeys
            //  jSch.setKnownHosts(new ByteArrayInputStream(knownHostPublicKey.getBytes()));
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(pw);
            session.connect(120000);
            channel = session.openChannel("shell");
            InputStream inputStream = channel.getInputStream();
            outputStream = channel.getOutputStream();
            channel.connect();
            if(outputStream == null) LOG.error("outputStream null");
            printOuput(inputStream);
            return 0;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public void disconnect() {
        write("exit");
        try{Thread.sleep(1000);}catch(Exception ee){}
        channel.disconnect();
        session.disconnect();
    }

    public void quit() {
        write("q");
    }

    public void pause() {
        write("p");
    }

    public void run(String command) { write(command);  }

    public String play(String video) {
        String command = videoPlayer + " " + video;
        LOG.info("command: " + command);
        writeln(command);
        return "playing " + video;
    }

    private void write(String command) {
        if(outputStream == null) LOG.error("outputStream null");
        if(command == null) LOG.error("command null");
        LOG.info("run " + command);
        try {
            outputStream.write((command).getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeln(String command) {
        write(command + "\n");
    }

    private void printOuput(InputStream in) {
        byte[] tmp=new byte[1024];
        while(true){
            try {
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    System.out.print(new String(tmp, 0, i));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(channel.isClosed()){
                try {
                    if(in.available()>0) continue;
                    System.out.println("exit-status: "+channel.getExitStatus());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            try{Thread.sleep(1000);}catch(Exception ee){}
        }
    }
}
