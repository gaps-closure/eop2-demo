package com.peratonlabs.closure.eop2.transcoder;

import org.opencv.core.*;
import com.peratonlabs.closure.eop2.WebSocketServer;
import com.peratonlabs.closure.eop2.video.requester.Request;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.HashMap;

public class Transcoder
{
    private static HashMap<String, Transcoder> clients = new HashMap<String, Transcoder>();
    
    private Request request;

    private Transcoder(Request request) {
        this.request = request;
    }
    
    public static void addClient(Request request) {
        Transcoder transcoder = new Transcoder(request);
        clients.put(request.getId(), transcoder);
    }
    
    public static void removeClient(Request request) {
        clients.remove(request.getId());
        WebSocketServer.close(request.getId());
    }

    public static void broadcast(Mat mat) {
        for (Transcoder transcoder : clients.values()) {
            Request request = transcoder.request;
            
            Mat mmm = mat.clone();
            if (!request.isColor())
                mmm = transcoder.convertGrayScale(mmm);
            
            if (request.isBlur())
                mmm = transcoder.addBlur(mmm, true);
            
            if (request.isScale())
                mmm = transcoder.changeImageScale(mmm, request);
            
            MatOfByte mem = new MatOfByte();
            Imgcodecs.imencode(".jpg", mmm, mem);
            byte[] memBytes = mem.toArray();

            WebSocketServer.send(request.getId(), memBytes);
        }
    }
    
    private Mat changeImageScale(Mat frame, Request request) {
        double scale = request.getScalePercentage() / (double) 100;
        Imgproc.resize(frame, frame, new Size(0, 0), scale, scale, Imgproc.INTER_AREA);

        return frame;
    }
    
    private Mat convertGrayScale(Mat frame) {
        Mat grayMat = new Mat();
        Imgproc.cvtColor(frame, grayMat, Imgproc.COLOR_RGB2GRAY);
        return grayMat;
    }

    private Mat addBlur(Mat frame) {
        Size size = new Size(45, 45);
        Point point = new Point(20, 30);
        Imgproc.blur(frame, frame, size, point, Core.BORDER_DEFAULT);
        return frame;
    }
    
    private Mat addBlur(Mat frame, boolean dummy) {
        // drawing a rectangle
        Point point1 = new Point(100, 100);
        Point point2 = new Point(500, 300);
        Scalar color = new Scalar(0, 255, 0);
        int thickness = 1;
        Imgproc.rectangle (frame, point1, point2, color, thickness);

        Rect rect = new Rect(point1, point2);
        Mat mask = frame.submat(rect);
        Imgproc.GaussianBlur(mask, mask, new Size(55, 55), 55); // or any other processing
        
        return frame;
    }
}
