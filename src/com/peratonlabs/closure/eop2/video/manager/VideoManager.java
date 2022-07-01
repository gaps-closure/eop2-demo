/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * Jun 23, 2022
 */
package com.peratonlabs.closure.eop2.video.manager;

import com.peratonlabs.closure.eop2.VideoServer;
import com.peratonlabs.closure.eop2.camera.CameraReader;
import com.peratonlabs.closure.eop2.camera.CameraType;
import com.peratonlabs.closure.eop2.transcoder.Transcoder;
import com.peratonlabs.closure.eop2.video.requester.Request;

public class VideoManager
{
    private static VideoManager instance;
    private static CameraReader camera;
    
    private CameraType cameraType = CameraType.WEB_CAMERA;
    private String cameraAddr = "127.0.0.1";
    private String cameraUser = "admin";
    private String cameraPassword = "Boosters";
    private int cameraDevId = 0;
    
    public static void main(final String[] args) {
        VideoManager manager = VideoManager.getInstance();

        VideoServer closure = VideoServer.getInstance();
        closure.start();
        
        manager.getOpts(args);
    }
    
    public static void handleRequest(Request request) {
        // Do permission checking here
        Transcoder.updateRequest(request);
    }
    
    public static VideoManager getInstance() {
        if (instance == null) {
            instance = new VideoManager();
        }
        return instance;
    }
    
    public static void startCamera() {
        if (camera != null)
            return;
            
        camera = new CameraReader();
        camera.start();
    }
    
    public static void stopCamera() {
        if (camera == null)
            return;
        
        camera.interrupt();
        camera = null;
    }
    
    private void getOpts(String[] args) {
        String arg;
        
        for (int i = 0; i < args.length; i++) {
            arg = args[i];
            switch (arg) {
            case "--cameraType":
            case "-t":
                cameraType = CameraType.getByName(args[++i]);
                break;
            case "--cameraAddr":
            case "-a":
                cameraAddr = args[++i];
                break;
            case "--cameraDev":
            case "-d":
                cameraDevId = Integer.parseInt(args[++i]);
                break;
            default:
                System.err.println("unknown option: " + arg);
                break;
            }
        }
    }
    
    public String getCameraURL() {
        return "rtsp://" + cameraUser + ":" + cameraPassword + "@" + cameraAddr;
    }
    
    public CameraType getCameraType() {
        return cameraType;
    }

    public void setCameraType(CameraType cameraType) {
        this.cameraType = cameraType;
    }

    public String getCameraAddr() {
        return cameraAddr;
    }

    public void setCameraAddr(String cameraAddr) {
        this.cameraAddr = cameraAddr;
    }

    public int getCameraDevId() {
        return cameraDevId;
    }

    public void setCameraDevId(int cameraDevId) {
        this.cameraDevId = cameraDevId;
    }
}
