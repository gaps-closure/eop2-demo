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
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import com.peratonlabs.closure.eop2.WebSocketServer;
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
        
        // Instantiating the VideoCapture class (camera:: 0)
        VideoCapture capture = new VideoCapture(0);
        
        // Reading the next video frame from the camera
        while (running.get()) {
            Mat mat = new Mat();
            capture.read(mat);
            
            // These are not strictly necessary
            capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
            capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);

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

            //WebSocketServer.broadcast(memBytes);
            Transcoder.broadcast(mat);

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
