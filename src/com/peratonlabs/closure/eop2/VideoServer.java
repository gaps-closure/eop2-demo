/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * Jun 26, 2022
 */
package com.peratonlabs.closure.eop2;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import static io.undertow.Handlers.resource;

import com.peratonlabs.closure.eop2.camera.CameraType;
import com.peratonlabs.closure.eop2.transcoder.WebSocketServer;
import com.peratonlabs.closure.eop2.video.requester.VideoRequester;

public class VideoServer 
{
    private static VideoServer instance;
    
    private CameraType cameraType = CameraType.WEB_CAMERA;
    private String cameraAddr = "127.0.0.1";
    private String cameraUser = "admin";
    private String cameraPassword = "Boosters";
    private int cameraDevId = 0;
   
    private HttpHandler handler = new PathHandler()
            .addPrefixPath("/video", WebSocketServer.createWebSocketHandler())
            .addPrefixPath("/", resource(new ClassPathResourceManager(VideoServer.class.getClassLoader()))
                                        .addWelcomeFiles("index.html"))
            .addPrefixPath("/request", Handlers.routing().post("/{request}", VideoRequester.createRequest()))
    ;
    
    public static VideoServer getInstance() {
        if (instance == null) {
            instance = new VideoServer();
        }
        return instance;
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
    
    public static void main(final String[] args) {
        VideoServer closure = getInstance();
        closure.getOpts(args);
        
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(closure.handler)
                .build();
        server.start();
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
