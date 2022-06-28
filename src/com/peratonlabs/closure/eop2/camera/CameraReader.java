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
package com.peratonlabs.closure.eop2.camera;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.peratonlabs.closure.eop2.ClosureServer;
import com.peratonlabs.closure.eop2.transcoder.Transcoder;

public class CameraReader implements Runnable
{
    private Thread worker;
    private AtomicBoolean running = new AtomicBoolean(true);
    
    public CameraReader() {
    }
    
    public void interrupt() {
        running.set(false);
        worker.interrupt();
    }
    
    public void start() {
        worker = new Thread(this);
        worker.start();
    }
 
    public void stop() {
        running.set(false);
    }
    
    @Override
    public void run() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        ClosureServer closure = ClosureServer.getInstance();
        
        boolean resize = false;
        VideoCapture capture = null;
        switch (closure.getCameraType()) {
        case WEB_CAMERA:
            capture = new VideoCapture(closure.getCameraDevId());
            break;
        case IP_CAMERA:
            capture = new VideoCapture(closure.getCameraURL());
            resize = true;
            break;
        default:
            System.err.println("Unsupported camera type: " + closure.getCameraType());
            System.exit(1);
            break;
        }
        
        // Reading the next video frame from the camera
        int count = 1;
        while (running.get()) {
            Mat mat = new Mat();
            capture.read(mat);
            
            if (resize)
                Imgproc.resize(mat, mat, new Size(0, 0), 0.25, 0.25, Imgproc.INTER_AREA);

            Transcoder.broadcast(mat);

            HighGui.imshow("Image", mat);
            HighGui.waitKey(1);
            System.out.println("Camera: " + count++);
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
    
//    public void setRes_1080p()
//    {
//        capture.set(3, 1920);
//        capture.set(4, 1080);
//    }
//    
//    public void setRes_720p()
//    {
//        capture.set(3, 1280);
//        capture.set(4, 720);
//    }
//    
//    public void setRes_480p()
//    {
//        capture.set(3, 640);
//        capture.set(4, 480);
//    }
//    
//    public void setRes(Integer height, Integer width)
//    {
//        capture.set(3, width);
//        capture.set(4, height);
//    }
    
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

    public static void main(String[] args) {
        CameraReader camera = new CameraReader();
        camera.run();
    }
}
