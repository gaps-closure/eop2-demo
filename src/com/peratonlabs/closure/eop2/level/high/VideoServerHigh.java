package com.peratonlabs.closure.eop2.level.high;

import com.peratonlabs.closure.eop2.level.VideoServer;

public class VideoServerHigh extends VideoServer implements Runnable
{
    private static boolean started = false;
    
    private VideoServerHigh(int port, String webroot, VideoServletHigh servlet) {
        this.port = port;
        this.webroot = webroot;
        this.servlet = servlet;
    }
    
    public static void startServer(int port, String webroot) {
        if (started) {
            return;
        }
        
        VideoServletHigh servlet = new VideoServletHigh();
        VideoServerHigh instance = new VideoServerHigh(port, webroot, servlet);
        Thread thread = new Thread(instance);
        thread.start();
        
        started = true;
    }
}
