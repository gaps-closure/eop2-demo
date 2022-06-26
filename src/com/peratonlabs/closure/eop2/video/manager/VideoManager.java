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
    
    public static WebSocketProtocolHandshakeHandler createWebSocketHandler() {
        return websocket(new WebSocketConnectionCallback() {
            @Override
            public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
                camera = new CameraReader(channel);
                Thread thread = new Thread(camera);
                thread.start();
                
                channel.getReceiveSetter().set(new AbstractReceiveListener() {
                    @Override
                    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                        WebSockets.sendText(message.getData(), channel, null);
                    }
                    
                    protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) {
                        // camera.setConnected(false);
                    }
                });
                channel.resumeReceives();
            }
        });
    }
}
