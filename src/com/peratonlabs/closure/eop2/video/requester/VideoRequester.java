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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.websocket.Session;

import com.peratonlabs.closure.eop2.VideoServer;
import com.peratonlabs.closure.eop2.video.manager.VideoManager;

public class VideoRequester
{
    private static VideoServer server;
    private static HashMap<String, VideoRequester> clients = new HashMap<String, VideoRequester>();
    
    private String id;
    private Session channel;

    private VideoRequester(String id) {
        this.id = id;
    }
    
    // south bound
    public static void handleMessage(Request request, Session channel) {
        String id = request.getId();
        VideoRequester client = clients.get(id);
        if (client == null) {
            client = new VideoRequester(id);
            clients.put(request.getId(), client);
        }
        client.onMessage(request, channel);
        handleRequest(request);
    }
    
    // south bound
    public static void handleRequest(Request request) {
        VideoManager.handleRequest(request);
    }
    
    // north bound
    public static void start() {
        if (server != null)
            return;
        
        server = VideoServer.getInstance();
        server.start();
    }
    
    // north bound
    public static void send(String id, byte[] data) {
        VideoRequester client = clients.get(id);
        if (client == null) {
            System.err.println("no such client: " + id);
            return;
        }
            
        try {
            client.getChannel().getBasicRemote().sendBinary(ByteBuffer.wrap(data));
        }
        catch (IOException e) {
            e.printStackTrace();
            client.setChannel(null);
        }
    }
    
    private void onMessage(Request request, Session channel) {
        String command = request.getCommand();
        if (command == null) {
            System.err.println("null command for " + id);
            return;
        }
        switch(command) {
        case "start":
            this.channel = channel;
            break;
        case "stop":
            try {
                if (channel != null)
                    channel.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                this.channel = null;
            }
            break;
        }
        System.out.println(this.getClass().getSimpleName() + ": " + command + " command processed");                        
    }
    
    public Session getChannel() {
        return channel;
    }

    public void setChannel(Session channel) {
        this.channel = channel;
    }
}
