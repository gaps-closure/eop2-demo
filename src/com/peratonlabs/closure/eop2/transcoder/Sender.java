/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Jul 1, 2022
 */
package com.peratonlabs.closure.eop2.transcoder;

import java.io.IOException;
import java.nio.ByteBuffer;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

public class Sender
{
    private WebSocketChannel channel;

    public Sender(WebSocketChannel channel) {
        this.channel = channel;
    }
    
    public void send(byte[] data) {
        try {
            WebSockets.sendBinaryBlocking(ByteBuffer.wrap(data), channel);
        }
        catch (IOException e) {
            e.printStackTrace();
            channel = null;
        }
    }
    
    public void close() {
        try {
            channel.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
