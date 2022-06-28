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

import java.util.HashSet;

import com.peratonlabs.closure.eop2.camera.CameraReader;
import com.peratonlabs.closure.eop2.transcoder.Transcoder;
import com.peratonlabs.closure.eop2.video.requester.Request;

public class VideoManager
{
    private static CameraReader camera;
    private static HashSet<String> clients = new HashSet<String>();

    public static void handleRequest(Request request) {
        switch(request.getCommand()) {
        case "start":
            if (camera == null) {
                camera = new CameraReader();
                camera.start();
            }
            clients.add(request.getId());
            Transcoder.addClient(request);
            break;
        case "stop":
            Transcoder.removeClient(request);
            clients.remove(request.getId());
            if (clients.isEmpty() && camera != null) {
                camera.interrupt();
                camera = null;
            }
            break;
        }
    }
    
    public static void removeClient(String id) {
        clients.remove(id);
        Transcoder.removeClient(id);
    }
}
