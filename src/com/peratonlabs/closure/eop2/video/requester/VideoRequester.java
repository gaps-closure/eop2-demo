/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * Jun 26, 2022
 */
package com.peratonlabs.closure.eop2.video.requester;

import static io.undertow.util.Headers.CONTENT_TYPE;
import static io.undertow.util.StatusCodes.OK;
import static java.nio.charset.StandardCharsets.UTF_8;

import io.undertow.io.Receiver.FullStringCallback;
import io.undertow.server.HttpHandler;

public class VideoRequester
{
    public static HttpHandler createRequest() {
        FullStringCallback callback = (exchange, payload) -> {
            System.out.println(payload);
            Request req = Request.fromJson(payload);

            String response = req.toJson();
            
            System.out.println(response);
            
            exchange.setStatusCode(OK);
            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(response, UTF_8);
        };

        return (exchange) -> exchange.getRequestReceiver().receiveFullString(callback, UTF_8);
    }
}
