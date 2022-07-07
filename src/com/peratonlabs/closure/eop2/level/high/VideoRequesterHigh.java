/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * Jun 26, 2022
 */
package com.peratonlabs.closure.eop2.level.high;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.Session;

import com.peratonlabs.closure.eop2.level.VideoRequester;
import com.peratonlabs.closure.eop2.video.requester.Request;

public class VideoRequesterHigh extends VideoRequester
{
    private static boolean serverStarted = false;
    private static HashMap<String, VideoRequesterHigh> clients = new HashMap<String, VideoRequesterHigh>();
    private static LinkedBlockingQueue<Request> queue = new LinkedBlockingQueue<Request>();

    private VideoRequesterHigh(String id) {
        this.id = id;
    }
    
    // south bound from VideoEndpointHigh
    public static void handleMessage(Request request, Session channel) {
        String id = request.getId();
        VideoRequesterHigh client = clients.get(id);
        if (client == null) {
            client = new VideoRequesterHigh(id);
            clients.put(request.getId(), client);
        }
        client.onMessage(request, channel);

        queue.add(request); // wait for VideoManager to retrieve it
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
    
    // north bound from VideoManager
    public static void start(int port, String webroot) {
        if (serverStarted)
            return;
        
        VideoServerHigh.startServer(port, webroot);
        serverStarted = true;
    }
    
    // north bound from Transcoder
    public static void send(String id, byte[] data) {
        VideoRequesterHigh client = clients.get(id);
        if (client == null) {
            System.err.println("no such client: " + id);
            return;
        }
        client.send(data);
    }
}
