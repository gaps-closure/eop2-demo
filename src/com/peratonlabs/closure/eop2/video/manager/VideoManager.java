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

import com.peratonlabs.closure.eop2.camera.CameraReader;
import com.peratonlabs.closure.eop2.transcoder.Transcoder;
import com.peratonlabs.closure.eop2.video.requester.Request;

public class VideoManager
{
    private static CameraReader camera;

    public static void handleRequest(Request request) {
        // Do permission checking here
        Transcoder.updateRequest(request);
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
}
