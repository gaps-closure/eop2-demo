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
import io.undertow.io.Receiver.FullStringCallback;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.websocket;
import static io.undertow.util.Headers.CONTENT_TYPE;
import static io.undertow.util.StatusCodes.OK;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.peratonlabs.closure.eop2.video.requester.Request;
import com.peratonlabs.closure.eop2.video.requester.VideoRequester;

public class VideoServer 
{
    private static VideoServer instance;
   
    private HttpHandler handler = new PathHandler()
            .addPrefixPath("/video", createWebSocketHandler())
            .addPrefixPath("/", resource(new ClassPathResourceManager(VideoServer.class.getClassLoader()))
                                        .addWelcomeFiles("index.html"))
            .addPrefixPath("/request", Handlers.routing().post("/{request}", createRequest()))
    ;
    
    public static VideoServer getInstance() {
        if (instance == null) {
            instance = new VideoServer();
        }
        return instance;
    }
    
    public void start() {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(handler)
                .build();
        server.start();
    }
    
    public static WebSocketProtocolHandshakeHandler createWebSocketHandler() {
        return websocket(new WebSocketConnectionCallback() {
            @Override
            public void onConnect(WebSocketHttpExchange exchange, WebSocketChannel channel) {
                channel.getReceiveSetter().set(new AbstractReceiveListener() {
                    @Override
                    protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) {
                        String msg = message.getData();
                        Request request = Request.fromJson(msg);
                        VideoRequester.handleMessage(request, channel);
                    }
                    
                    protected void onClose(WebSocketChannel webSocketChannel, StreamSourceFrameChannel channel) {
                        // TODO:
                    }
                });
                channel.resumeReceives();
            }
        });
    }
    
    public static HttpHandler createRequest() {
        FullStringCallback callback = (exchange, payload) -> {
            System.out.println(payload);
            Request req = Request.fromJson(payload);

            VideoRequester.handleRequest(req);
            
            String response = req.toJson();
            
            System.out.println(response);
            
            exchange.setStatusCode(OK);
            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(response, UTF_8);
        };

        return (exchange) -> exchange.getRequestReceiver().receiveFullString(callback, UTF_8);
    }
}
