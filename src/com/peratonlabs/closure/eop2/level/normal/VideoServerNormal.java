package com.peratonlabs.closure.eop2.level.normal;

import com.peratonlabs.closure.eop2.level.VideoServer;

public class VideoServerNormal extends VideoServer implements Runnable
{
    private static boolean started = false;
    
    private VideoServerNormal(int port, String webroot, VideoServletNormal servlet) {
        this.port = port;
        this.webroot = webroot;
        this.servlet = servlet;
    }
    
    public static void startServer(int port, String webroot) {
        if (started) {
            return;
        }
        
        VideoServletNormal servlet = new VideoServletNormal();
        VideoServerNormal instance = new VideoServerNormal(port, webroot, servlet);
        Thread thread = new Thread(instance);
        thread.start();
        
        started = true;
    }
}
