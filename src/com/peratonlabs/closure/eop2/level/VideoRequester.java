/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * Jun 26, 2022
 */
package com.peratonlabs.closure.eop2.level;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.websocket.Session;

import com.peratonlabs.closure.eop2.video.requester.Request;

public abstract class VideoRequester
{
    protected String id;
    protected Session channel;
    
    protected void onMessage(Request request, Session channel) {
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
