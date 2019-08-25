//-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8000

package mediathekRaspi.videoPlayback.service;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

    public int start() {
        LOG.info("user: " + userName + " tries to connect via ssh");
        LOG.debug("pw: " + pw);

        try {
            JSch jSch = new JSch();
            String knownHostPublicKey = "localhost ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDCTCPlgreQqpExO6Y7swmGsu40iNRPTkYMPqYU32NZfpeSWCbxumGqcpgBndxAEYp3Jj0HOiPGxqoGp39QqzPeBF+QO1eOf3KctzmbvevjD/asF5aEw5DRYruAnblt0/U1t3ywR1FCjsF2+pbgT08Coy74uplW5K9p3FEl/+2GQbMYO+PaGVVrge9d9n6+vGRT7wWTpGWuYIshc4cKCzCk8H3MC6kD2BiGim+m8Tc5HfTBDsouThxoLiSLUV3S/xkh+VtExjqZQuYOqjONnw82WjxGc6iL8LO7AG33Er5pVjOj6i6fqsgK2Ao3JBiHIFdYntfS43MIbZCgX5/75k0f";
          //  jSch.setKnownHosts(new ByteArrayInputStream(knownHostPublicKey.getBytes()));
            session = jSch.getSession(userName, "localhost", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(pw);
            session.connect(120000);
            channel = session.openChannel("shell");
            InputStream inputStream = channel.getInputStream();
            outputStream = channel.getOutputStream();
            channel.connect();
            if(outputStream == null) LOG.error("outputStream null");
            /*while(true) {
                if (channel.isClosed()) break;
                try{Thread.sleep(1000);}catch (Exception e) {}
            }*/
            printOuput(inputStream);
            return 0;
        } catch (Exception e) {
            LOG.error(e.getMessage());
            e.printStackTrace();
            return -1;
        }
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

    public void write(String command) {
        if(outputStream == null) LOG.error("outputStream null");
        if(command == null) LOG.error("command null");
        try {
            outputStream.write((command).getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeln(String command) {
        write(command + "\n");
    }

    public void exit() {
        write("exit");
        try{Thread.sleep(1000);}catch(Exception ee){}
        channel.disconnect();
        session.disconnect();
    }

    public String play(String video) {
        String command = videoPlayer + " " + video;
        LOG.info("command: " + command);
        writeln(command);
        return "playing " + video;
    }

}
