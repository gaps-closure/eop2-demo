/**
 * Copyright (c) 2022 All rights reserved
 * Peraton Labs, Inc.
 *
 * Proprietary and confidential. Unauthorized copy or use of this file, via
 * any medium or mechanism is strictly prohibited. 
 *
 * @author tchen
 *
 * Jun 22, 2022
 */
package com.peratonlabs.closure.eop2;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;

public class CameraReader implements Runnable
{
    private WebSocketChannel channel;
    private boolean connected = true;
    
    public CameraReader() {
    }
    
    public CameraReader(WebSocketChannel channel) {
        this.channel = channel;
    }
    
    @Override
    public void run() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        // Instantiating the VideoCapture class (camera:: 0)
        VideoCapture capture = new VideoCapture(0);
        
        // Reading the next video frame from the camera
        while (channel == null || !channel.isCloseFrameReceived()) {
            Mat mat = new Mat();
            capture.read(mat);

            long imgSize = mat.total() * mat.elemSize();

            byte[] bytes = new byte[(int) imgSize];
            mat.get(0,0,bytes);

//            System.out.println("image size : " + imgSize + " rows: " + mat.rows() + " cols: " + mat.cols() + " :" + mat.type());
//            System.out.println(mat.toString());

            try {
//                for (int i = 0; i < 6; i++) {
//                    System.out.print((((int)bytes[i]) & 0xff) + ", ");
//                }
//                System.out.println();
                if (channel != null && !channel.isCloseFrameReceived())
                    WebSockets.sendBinaryBlocking(ByteBuffer.wrap(bytes), channel);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
            HighGui.imshow("Image", mat);
            HighGui.waitKey(1);
        }
        capture.release();
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    public static void main(String[] args) {
        CameraReader camera = new CameraReader();
        camera.run();
    }
}
