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
import com.peratonlabs.closure.eop2.video.requester.VideoRequester;

public class ClosureServer 
{
    private HttpHandler handler = new PathHandler()
            .addPrefixPath("/video", WebSocketServer.createWebSocketHandler())
            .addPrefixPath("/", resource(new ClassPathResourceManager(ClosureServer.class.getClassLoader()))
                                        .addWelcomeFiles("index.html"))
            .addPrefixPath("/request", Handlers.routing().post("/{request}", VideoRequester.createRequest()))
    ;
    
    public static void main(final String[] args) {
        ClosureServer closure = new ClosureServer();
        
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(closure.handler)
                .build();
        server.start();
    }

}
