package com.peratonlabs.closure.eop2.level.normal;

import com.peratonlabs.closure.eop2.level.VideoServer;

public class VideoServerNormal extends VideoServer implements Runnable
{
    private static boolean started = false;
    
    private VideoServerNormal(int port, String webroot) {
        this.port = port;
        this.webroot = webroot;
    }
    
    public static void startServer(int port, String webroot) {
        if (started) {
            return;
        }
        
        VideoServerNormal instance = new VideoServerNormal(port, webroot);
        Thread thread = new Thread(instance);
        thread.start();
        
        started = true;
    }
}
