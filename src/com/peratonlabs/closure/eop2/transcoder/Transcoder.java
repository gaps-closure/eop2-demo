package com.peratonlabs.closure.eop2.transcoder;

import org.opencv.core.*;

import com.peratonlabs.closure.eop2.video.manager.VideoManager;
import com.peratonlabs.closure.eop2.video.requester.Request;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Transcoder implements Runnable
{
    private static HashMap<String, Transcoder> clients = new HashMap<String, Transcoder>();
    
    private Request request;
    private Thread worker;
    private LinkedBlockingQueue<Mat> queue = new LinkedBlockingQueue<Mat>();
    
    private Transcoder(Request request) {
        this.request = request;
    }
    
    public static void updateRequest(Request request) {
        Transcoder transcoder = clients.get(request.getId());
        if (transcoder == null) {
            transcoder = new Transcoder(request);
            clients.put(request.getId(), transcoder);
            return;
        }
        transcoder.request.update(request);
    }
    
    public static void addClient(Request request) {
        Transcoder transcoder = new Transcoder(request);
        
        clients.put(request.getId(), transcoder);
    }
    
    public static void runCommand(Request request) {
        if (request == null) {
            System.err.println("null request");
            return;
        }
        
        String id = request.getId();
        Transcoder transcoder = clients.get(id);
        if (transcoder == null) {
            System.out.println("Warning: transcoder not found for " + id);
            request.setColor(true);
            updateRequest(request);
            transcoder = clients.get(id);
        }
        
        String command = request.getCommand();
        if (command == null) {
            System.err.println("null command for " + id);
            return;
        }
        switch(command) {
        case "start":
            transcoder.start();
            VideoManager.startCamera();
            break;
        case "stop":
            transcoder.interrupt();
            break;
        }
        System.out.println(command + " command processed");                        
    }
    
    public static void removeClient(Request request) {
        removeClient(request.getId());
    }
    
    public static void removeClient(String id) {
        Transcoder transcoder = clients.remove(id);
        transcoder.interrupt();
        
        WebSocketServer.close(id);
    }

    public static void broadcast(Mat mat) {
        for (Transcoder transcoder : clients.values()) {
            transcoder.queue.add(mat);
            //transcoder.show(mat);
        }
    }
    
    int cnt = 1;
    private void show(Mat mat) {
        Mat mmm = mat.clone();
        if (!request.isColor())
            mmm = convertGrayScale(mmm);
        
        if (request.isBlur())
            mmm = addBlur(mmm, false);
        
        if (request.isScale())
            mmm = changeImageScale(mmm, request);
        
        MatOfByte mem = new MatOfByte();
        Imgcodecs.imencode(".jpg", mmm, mem);
        byte[] memBytes = mem.toArray();

        WebSocketServer.send(request.getId(), memBytes);
        
        if (request.getDelay() > 0)
            try {
                Thread.sleep(request.getDelay());
            }
            catch (InterruptedException e) {
                e.printStackTrace();
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

    private Mat addBlur(Mat frame, boolean whole) {
        if (whole) {
            Size size = new Size(45, 45);
            Point point = new Point(20, 30);
            Imgproc.blur(frame, frame, size, point, Core.BORDER_DEFAULT);
        }
        else {
            // drawing a rectangle
            Point point1 = new Point(100, 100);
            Point point2 = new Point(500, 300);
            Scalar color = new Scalar(0, 255, 0);
            int thickness = 1;
            Imgproc.rectangle (frame, point1, point2, color, thickness);

            Rect rect = new Rect(point1, point2);
            Mat mask = frame.submat(rect);
            Imgproc.GaussianBlur(mask, mask, new Size(55, 55), 55); // or any other processing
        }
        
        return frame;
    }
    
    public void interrupt() {
        worker.interrupt();
    }
    
    public void start() {
        worker = new Thread(this);
        worker.start();
    }
 
    @Override
    public void run() {
        while (true) {
            try {
                Mat mat = queue.take();
                show(mat);
            }
            catch (InterruptedException e) {
                break;
            }
        }
    }
}

//long imgSize = mat.total() * mat.elemSize();
//
//byte[] bytes = new byte[(int) imgSize];
//mat.get(0, 0, bytes);

//          Mat m2 = new Mat(mat.rows(), mat.cols(), mat.type());
//          m2.put(0,0, bytes);
