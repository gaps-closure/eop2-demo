/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.peratonlabs.closure.eop2.server;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.StreamSourceFrameChannel;
import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import io.undertow.websockets.WebSocketConnectionCallback;
import io.undertow.websockets.WebSocketProtocolHandshakeHandler;
import io.undertow.websockets.spi.WebSocketHttpExchange;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.resource;
import static io.undertow.Handlers.websocket;

import java.nio.file.Paths;

import com.peratonlabs.closure.eop2.CameraReader;

/**
 * @author Stuart Douglas
 */
//@UndertowExample("Web Sockets")
public class TestServer 
{
    private CameraReader camera;
    
    private HttpHandler ROUTES = new RoutingHandler()
            .get("/",  
//                    RoutingHandlers.plainTextHandler("GET - My Homepage"))
            resource(new ClassPathResourceManager(TestServer.class.getClassLoader(), 
                     TestServer.class.getPackage())).addWelcomeFiles("index.html"))
            .get("/video", createWebSocketHandler())
            .get("/about", RoutingHandlers.plainTextHandler("GET - about"))
            .post("/about", RoutingHandlers.plainTextHandler("POST - about"))
            .get("/new*", RoutingHandlers.plainTextHandler("GET - new*"))
            .setFallbackHandler(RoutingHandlers::notFoundHandler);
    
    private WebSocketProtocolHandshakeHandler createWebSocketHandler() {
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
    
    private void read() {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(
                    path()
                        .addPrefixPath("/video", createWebSocketHandler())
                        .addPrefixPath("/", resource(new ClassPathResourceManager(TestServer.class.getClassLoader()))
                                                    .addWelcomeFiles("index.html"))
                        
                        .addPrefixPath("/about", RoutingHandlers.plainTextHandler("GET - about"))
                        .addPrefixPath("/controller", Handlers.routing()
                                .post("/{id}", exchange -> {
                                    String id = exchange.getQueryParameters().get("id").getFirst();
                                    System.out.println("############ " + id);
                                }))
                        // REST API path
                        .addPrefixPath("/api", Handlers.routing()
                            .get("/customers", exchange -> {System.out.println("customers");})
                            .delete("/customers/{customerId}", exchange -> {System.out.println("delete");})
                            .setFallbackHandler(exchange -> {System.out.println("fallback");}))

                        // Redirect root path to /static to serve the index.html by default
//                        .addExactPath("/", Handlers.redirect("/static"))

                        // Serve all static files from a folder
//                        .addPrefixPath("/static", new ResourceHandler(
//                            new PathResourceManager(Paths.get("/path/to/www/"), 100))
//                            .setWelcomeFiles("index.html"))                        
                )
                .build();
        server.start();
    }
    
    public static void main(final String[] args) {
        TestServer server = new TestServer();
        server.read();
    }

}
