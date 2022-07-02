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

import java.util.HashMap;

import org.opencv.core.Mat;

import com.peratonlabs.closure.eop2.camera.CameraReader;
import com.peratonlabs.closure.eop2.camera.CameraType;
import com.peratonlabs.closure.eop2.transcoder.Transcoder;
import com.peratonlabs.closure.eop2.video.requester.Request;
import com.peratonlabs.closure.eop2.video.requester.VideoRequester;

public class VideoManager
{
    private static HashMap<String, Transcoder> clients = new HashMap<String, Transcoder>();
    
    private static VideoManager instance;
    private static CameraReader camera;
    
    private CameraType cameraType = CameraType.WEB_CAMERA;
    private String cameraAddr = "127.0.0.1";
    private String cameraUser = "admin";
    private String cameraPassword = "Boosters";
    private int cameraDevId = 0;
    
    public static void main(final String[] args) {
        VideoRequester.start();
        
        VideoManager manager = VideoManager.getInstance();
        manager.getOpts(args);
    }
    
    // from VideoRequester
    public static void handleRequest(Request request) {
        if (request == null) {
            System.err.println("null request");
            return;
        }
        
        String id = request.getId();
        Transcoder transcoder = clients.get(id);
        if (transcoder == null) {
            transcoder = new Transcoder(id);
            clients.put(id, transcoder);
        }
        
        String cmd = request.getCommand();
        if (cmd == null) {
            // Do permission checking here
            updateRequest(transcoder, request);
        }
        else {
            runCommand(transcoder, request);
        }
    }
    
    private static void updateRequest(Transcoder transcoder, Request request) {
        transcoder.getRequest().update(request);
    }
    
    private static void runCommand(Transcoder transcoder, Request request) {
        String command = request.getCommand();
        if (command == null) {
            System.err.println("null command for " + request.getId());
            return;
        }
        switch(command) {
        case "start":
            transcoder.start();
            startCamera();
            break;
        case "stop":
            transcoder.interrupt();
            clients.remove(request.getId());
            stopCamera();
            break;
        }
        System.out.println("VideoManager: " + command + " command processed");                        
    }
    
    public static void removeClient(Request request) {
        removeClient(request.getId());
    }
    
    public static void removeClient(String id) {
        Transcoder transcoder = clients.remove(id);
        transcoder.interrupt();
    }

    public static void broadcast(Mat mat) {
        for (Transcoder transcoder : clients.values()) {
            transcoder.add(mat);
        }
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
        if (!clients.isEmpty())
            return;
        
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
