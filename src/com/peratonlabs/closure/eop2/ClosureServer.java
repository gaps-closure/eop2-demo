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
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import static io.undertow.Handlers.path;
import static io.undertow.Handlers.resource;
import com.peratonlabs.closure.eop2.video.manager.VideoManager;
import com.peratonlabs.closure.eop2.video.requester.VideoRequester;

public class ClosureServer 
{
    private HttpHandler ROUTES = new RoutingHandler()
            .get("/",  
//                    RoutingHandlers.plainTextHandler("GET - My Homepage"))
            resource(new ClassPathResourceManager(ClosureServer.class.getClassLoader(), 
                     ClosureServer.class.getPackage())).addWelcomeFiles("index.html"))
            .get("/video", VideoManager.createWebSocketHandler())
            .get("/about", RoutingHandlers.plainTextHandler("GET - about"))
            .post("/about", RoutingHandlers.plainTextHandler("POST - about"))
            .get("/new*", RoutingHandlers.plainTextHandler("GET - new*"))
            .setFallbackHandler(RoutingHandlers::notFoundHandler);
    
    private void serve() {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(
                    path()
                        .addPrefixPath("/video", VideoManager.createWebSocketHandler())
                        .addPrefixPath("/", resource(new ClassPathResourceManager(ClosureServer.class.getClassLoader()))
                                                    .addWelcomeFiles("index.html"))
                        
                        .addPrefixPath("/about", RoutingHandlers.plainTextHandler("GET - about"))
                        .addPrefixPath("/controller", Handlers.routing()
                                .post("/{id}", exchange -> {
                                    String id = exchange.getQueryParameters().get("id").getFirst();
                                    System.out.println("############ " + id);
                                })
                                )
                        .addPrefixPath("/request", Handlers.routing()
                                .post("/{request}", VideoRequester.createRequest())
                                )
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
        ClosureServer server = new ClosureServer();
        server.serve();
    }

}
