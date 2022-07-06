/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * Jun 26, 2022
 */
package com.peratonlabs.closure.eop2.level.normal;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.Session;

import com.peratonlabs.closure.eop2.level.VideoRequester;
import com.peratonlabs.closure.eop2.video.requester.Request;

public class VideoRequesterNormal extends VideoRequester
{
    private static VideoServerNormal server;
    private static HashMap<String, VideoRequesterNormal> clients = new HashMap<String, VideoRequesterNormal>();
    private static LinkedBlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();

    private VideoRequesterNormal(String id) {
        this.id = id;
    }
    
    // south bound
    public static void handleMessage(Request request, Session channel) {
        String id = request.getId();
        VideoRequesterNormal client = clients.get(id);
        if (client == null) {
            client = new VideoRequesterNormal(id);
            clients.put(request.getId(), client);
        }
        client.onMessage(request, channel);
        handleRequest(request);
    }
    
    // south bound
    public static void handleRequest(Request request) {
        // VideoManager.handleRequest(request);
        queue.add(request); // wait for video manager to retrieve it
    }
    
    // VideoManager retrieves requests by calling this function
    public static Request getRequest() {
        if (!queue.isEmpty()) {
            try {
                Request request = queue.take();
                return request;
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    // north bound
    public static void start(String webroot) {
        if (server != null)
            return;
        
        server = new VideoServerNormal();
        server.start(webroot);
    }
    
    // north bound
    public static void send(String id, byte[] data) {
        VideoRequesterNormal client = clients.get(id);
        if (client == null) {
            System.err.println("no such client: " + id);
            return;
        }
        client.send(data);
    }
}
