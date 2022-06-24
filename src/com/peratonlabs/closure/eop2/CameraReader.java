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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.jcodec.scale.RgbToYuv420;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
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
            
            MatOfByte mem = new MatOfByte();
            Imgcodecs.imencode(".jpg", mat, mem);
            byte[] memBytes = mem.toArray();
//            
//            MatOfByte mob = new MatOfByte(memBytes);
//            Mat xxx = Imgcodecs.imdecode(mob, Imgcodecs.IMREAD_COLOR);
//            HighGui.imshow("Image", xxx);
//            HighGui.waitKey();

            long imgSize = mat.total() * mat.elemSize();

            byte[] bytes = new byte[(int) imgSize];
            mat.get(0, 0, bytes);
            
//          Mat m2 = new Mat(mat.rows(), mat.cols(), mat.type());
//          m2.put(0,0, bytes);

            try {
                if (channel != null && !channel.isCloseFrameReceived())
                    WebSockets.sendBinaryBlocking(ByteBuffer.wrap(memBytes), channel);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
            HighGui.imshow("Image", mat);
            HighGui.waitKey(1);
        }
        capture.release();
    }
    
    public BufferedImage toBufferedImage(Mat m) {
        if (!m.empty()) {
            int type = BufferedImage.TYPE_BYTE_GRAY;
            if (m.channels() > 1) {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }
            
            int bufferSize = m.channels() * m.cols() * m.rows();
            byte[] b = new byte[bufferSize];
            m.get(0, 0, b); // get all the pixels
            
            BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
            
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(b, 0, targetPixels, 0, b.length);
            
            return image;
        }
        
        return null;
    }
    /*
    private static void png2avc(String pattern, String out) throws IOException {
        FileChannel sink = null;
        try {
          sink = new FileOutputStream(new File(out)).getChannel();
          H264Encoder encoder = new H264Encoder();
          RgbToYuv420 transform = new RgbToYuv420(0, 0);

          int i;
          for (i = 0; i < 10000; i++) {
            File nextImg = new File(String.format(pattern, i));
            if (!nextImg.exists())
              continue;
            BufferedImage rgb = ImageIO.read(nextImg);
            Picture yuv = Picture.create(rgb.getWidth(), rgb.getHeight(), ColorSpace.YUV420);
            transform.transform(AWTUtil.fromBufferedImage(rgb), yuv);
            ByteBuffer buf = ByteBuffer.allocate(rgb.getWidth() * rgb.getHeight() * 3);

            ByteBuffer ff = encoder.encodeFrame(buf, yuv);
            sink.write(ff);
          }
          if (i == 1) {
            System.out.println("Image sequence not found");
            return;
          }
        } finally {
          if (sink != null)
            sink.close();
        }
      }
      */

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    public static void main(String[] args) {
        CameraReader camera = new CameraReader();
        camera.run();
    }
}
