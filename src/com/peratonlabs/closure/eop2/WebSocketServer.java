/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Jun 27, 2022
 */
package com.peratonlabs.closure.eop2;

import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import static io.undertow.Handlers.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class WebSocketServer 
{
    private static HashMap<String, WebSocketChannel> channels = new HashMap<String, WebSocketChannel>();
    private static HashMap<WebSocketChannel, String> ids = new HashMap<WebSocketChannel, String>();

    public static WebSocketProtocolHandshakeHandler createWebSocketHandler() {
        return websocket(new WebSocketConnectionCallback() {
            @Override
            public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
                channel.getReceiveSetter().set(new AbstractReceiveListener() {
                    @Override
                    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                        // WebSockets.sendText(message.getData(), channel, null);
                        String id = message.getData();
                        channels.put(id, channel);
                        ids.put(channel, id);
System.out.println("channel created: " + id);                        
                    }
                    
                    protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) {
                        // camera.setConnected(false);
                        String id = ids.get(webSocketChannel);
                        if (id == null)
                            System.err.println("no such channel");
                        else {
                            channels.remove(id);
                        }
                        ids.remove(webSocketChannel);
                    }
                });
                channel.resumeReceives();
            }
        });
    }
    
    public static void broadcast(byte[] memBytes) {
        for (WebSocketChannel channel : channels.values()) {
            try {
                if (channel != null && !channel.isCloseFrameReceived())
                    WebSockets.sendBinaryBlocking(ByteBuffer.wrap(memBytes), channel);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
