/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Jul 2, 2022
 */
package com.peratonlabs.closure.eop2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.peratonlabs.closure.eop2.video.requester.Request;
import com.peratonlabs.closure.eop2.video.requester.RequestDecoder;
import com.peratonlabs.closure.eop2.video.requester.RequestEncoder;

@ServerEndpoint( 
        value="/video/{id}", 
        decoders = RequestDecoder.class, 
        encoders = RequestEncoder.class )
public class CommandEndpoint {
 
    private Session session;
    private static Set<CommandEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();

    @OnOpen
    public void onOpen(
      Session session, 
      @PathParam("id") String id) throws IOException {
 
        this.session = session;
        chatEndpoints.add(this);
        users.put(session.getId(), id);
//System.out.println("id = " + id);
//        Message message = new Message();
//        message.setFrom(id);
//        message.setContent("Connected!");
//        try {
//            System.out.println("onOpen " + message);
//            broadcast(message);
//        }
//        catch (IOException | EncodeException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @OnMessage
    public void onMessage(Session session, Request request) 
      throws IOException {
        
 System.out.println(request.getCommand());
 byte[] aaa = {1};
 try {
    //session.getBasicRemote().sendObject(aaa);
    session.getBasicRemote().sendBinary(ByteBuffer.wrap(aaa));
}
catch (IOException e) {
    e.printStackTrace();
}
//        message.setFrom(users.get(session.getId()));
//        try {
//            broadcast(message);
//        }
//        catch (IOException | EncodeException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
 
        chatEndpoints.remove(this);
//        Message message = new Message();
//        message.setFrom(users.get(session.getId()));
//        message.setContent("Disconnected!");
//        try {
//            broadcast(message);
//        }
//        catch (IOException | EncodeException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(Message message) 
      throws IOException, EncodeException {
 
        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                      sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
