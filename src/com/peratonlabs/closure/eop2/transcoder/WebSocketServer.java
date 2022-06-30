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
package com.peratonlabs.closure.eop2.transcoder;

import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import static io.undertow.Handlers.websocket;

import com.peratonlabs.closure.eop2.video.requester.Request;

public class WebSocketServer 
{
    public static WebSocketProtocolHandshakeHandler createWebSocketHandler() {
        return websocket(new WebSocketConnectionCallback() {
            @Override
            public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
                channel.getReceiveSetter().set(new AbstractReceiveListener() {
                    @Override
                    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                        String msg = message.getData();
                        Request req = Request.fromJson(msg);
                        Transcoder.runCommand(req, channel);
                    }
                    
                    protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) {
                        // TODO:
                    }
                });
                channel.resumeReceives();
            }
        });
    }
}

    