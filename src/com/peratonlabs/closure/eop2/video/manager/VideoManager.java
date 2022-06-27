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

import static io.undertow.Handlers.websocket;

import com.peratonlabs.closure.eop2.camera.CameraReader;
import com.peratonlabs.closure.eop2.video.requester.Request;

import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.spi.WebSocketHttpExchange;

public class VideoManager
{
    private static CameraReader camera;
    
    public static void handleRequest(Request request) {
        switch(request.getCommand()) {
        case "start":
            if (camera == null) {
                camera = new CameraReader();
                Thread thread = new Thread(camera);
                thread.start();
            }
            break;
        case "stop":
            break;
        }
    }
}
